package com.tinf.qmobile.parser;

import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.tinf.qmobile.App;
import com.tinf.qmobile.BuildConfig;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.journal.Journal_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.model.matter.Period_;
import com.tinf.qmobile.service.Jobs;
import com.tinf.qmobile.utility.RandomColor;
import com.tinf.qmobile.utility.User;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Date;
import io.objectbox.exception.NonUniqueResultException;
import io.objectbox.query.QueryBuilder;

import static com.tinf.qmobile.utility.Utils.getDate;

public class JournalParser extends BaseParser {

    public JournalParser(int page, int pos, boolean notify, OnFinish onFinish, OnError onError) {
        super(page, pos, notify, onFinish, onError);
    }

    @Override
    public void parse(Document document) {

        RandomColor colors = new RandomColor();

        Date today = new Date();

        Elements dates = document.getElementsByTag("option");

        String[] years = new String[dates.size() - 1];

        for (int i = 0; i < dates.size() - 1; i++) {
            years[i] = dates.get(i + 1).text();
        }

        User.setYears(years);

        Element body = document.getElementsByTag("tbody").get(12);
        Elements contents = body.select("table.conteudoTexto");

        if (!BuildConfig.DEBUG) {
            Crashlytics.log(Log.ERROR, "Journals", body.toString());
        }

        for (int i = 0; i < contents.size(); i++) {

            Elements header = contents.eq(i).parents().eq(0).parents().eq(0);

            Element reports = header.first().child(1); //TODO

            String description = header.first().child(0).text();

            boolean isFirstParse = false;

            Matter matter = matterBox.query()
                    .equal(Matter_.description_, description).and()
                    .equal(Matter_.year_, User.getYear(pos)).and()
                    .equal(Matter_.period_, User.getPeriod(pos))
                    .build().findUnique();

            if (matter == null) {
                matter = new Matter(description, colors.getColor(), User.getYear(pos), User.getPeriod(pos));
                matterBox.put(matter);
                isFirstParse = true;
            }

            Element content = null;

            if (header.next().eq(0) != null) {
                content = header.next().eq(0).first();
            }

            int periodCount = 0;

            while (content != null && content.child(0).child(0).is("div")) {

                String periodTitle = content.child(0).child(0).ownText();
                Element tableGrades = content.child(0).child(1).child(0);
                Elements grades = tableGrades.getElementsByClass("conteudoTexto");
                content = content.nextElementSibling();

                Period period = null;

                if (periodCount <= matter.periods.size() - 1) {
                    period = matter.periods.get(periodCount);
                } else {
                    period = new Period(periodTitle);
                    if (periodTitle.contains("Exame") || periodTitle.contains("Reavaliação") || periodTitle.contains("Final") || periodTitle.contains("Conceito")) {
                        period.setSub();
                    }
                }

                periodCount++;

                for (int j = 0; j < grades.size(); j++) {
                    String dateString = formatDate(grades.eq(j).first().child(1).text());
                    String infos = grades.eq(j).first().child(1).text();

                    //Log.i(TAG, infos);


                    String t = formatType(infos);

                    //Log.i(TAG, infos);

                    int type = Journal.Type.AVALIACAO.get();

                    if (t.equals("Prova")) {
                        type = Journal.Type.PROVA.get();
                    } else if (t.equals("Diarios") || t.equals("Trabalho")) {
                        type = Journal.Type.TRABALHO.get();
                    } else if (t.contains("Qualitativa")) {
                        type = Journal.Type.QUALITATIVA.get();
                    } else if (t.equals("Exercício")) {
                        type = Journal.Type.EXERCICIO.get();
                    }

                    String title = formatJournalTitle(infos);
                    String weightString = formatGrade(formatNumber(grades.eq(j).first().child(2).text()));
                    String maxString = formatGrade(formatNumber(grades.eq(j).first().child(3).text()));
                    String gradeString = formatGrade(formatNumber(grades.eq(j).first().child(4).text()));

                    float grade, weight, max;
                    long date;

                    grade = gradeString.isEmpty() ? -1 : Float.parseFloat(gradeString);

                    weight = weightString.isEmpty() ? -1 : Float.parseFloat(weightString);

                    max = maxString.isEmpty() ? -1 : Float.parseFloat(maxString);

                    date = dateString.isEmpty() ? -1 : getDate(dateString, false);

                    if (date != -1) {

                        try {
                            QueryBuilder<Journal> builder = journalBox.query()
                                    .equal(Journal_.title, title).and()
                                    .equal(Journal_.type_, type).and()
                                    .between(Journal_.startTime, date, date).and()
                                    .between(Journal_.weight_, weight, weight).and()
                                    .between(Journal_.max_, max, max);

                            /*builder.link(Journal_.matter)
                                    .equal(Matter_.description_, description).and()
                                    .equal(Matter_.year_, User.getYear(pos)).and()
                                    .equal(Matter_.period_, User.getPeriod(pos));*/

                            builder.link(Journal_.period)
                                    .equal(Period_.id, period.id);

                            Journal search = builder.build().findUnique();

                            if (search == null) {

                                Journal newJournal = new Journal(title, grade, weight, max, date, type, period, matter, isFirstParse);

                                period.journals.add(newJournal);
                                journalBox.put(newJournal);

                                if (notify && grade != -1 && date <= today.getTime()) {
                                    sendNotification(newJournal);
                                }
                            } else {
                                if (search.getGrade_() != grade) {
                                    search.setGrade(grade);
                                    journalBox.put(search);

                                    if (notify) {
                                        sendNotification(search);
                                    }
                                }
                            }

                        } catch (NonUniqueResultException e) {
                            e.printStackTrace();
                        }
                    }
                }

                period.matter.setTarget(matter);
                matter.periods.add(period);
                periodBox.put(period);
            }

            matterBox.put(matter);
        }
    }

    private void sendNotification(Journal journal) {
        Intent intent = new Intent(App.getContext(), EventViewActivity.class);
        intent.putExtra("ID", journal.id);
        intent.putExtra("TYPE", CalendarBase.ViewType.JOURNAL);

        Jobs.displayNotification(App.getContext(), journal.getMatter(), journal.getTitle(),
                App.getContext().getResources().getString(R.string.title_diarios), (int) journal.id, intent);
    }

    private String formatType(String s) {
        s = s.substring(s.indexOf(",") + 1);
        return s.substring(0, s.indexOf(':')).trim();
    }

    private String formatJournalTitle(String s) {
        return s.substring(s.indexOf(":") + 1).trim();
    }

    private String formatDate(String s) {
        return s.substring(0, s.indexOf(',')).trim();
    }

}
