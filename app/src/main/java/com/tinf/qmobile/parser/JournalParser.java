package com.tinf.qmobile.parser;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.tinf.qmobile.App;
import com.tinf.qmobile.BuildConfig;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.model.calendar.base.CalendarBase;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.journal.Journal_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.network.OnEvent;
import com.tinf.qmobile.service.Jobs;
import com.tinf.qmobile.utility.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;
import java.util.Random;

import io.objectbox.Box;
import io.objectbox.exception.NonUniqueResultException;
import io.objectbox.query.QueryBuilder;

import static com.tinf.qmobile.model.calendar.Utils.getDate;
import static com.tinf.qmobile.network.OnResponse.PG_DIARIOS;

public class JournalParser extends AsyncTask<String, Void, Void> {
    private final static String TAG = "JournalParser";
    private OnFinish onFinish;
    private int pos;
    private boolean notify;

    public JournalParser(int pos, boolean notify, OnFinish onFinish) {
        this.pos = pos;
        this.notify = notify;
        this.onFinish = onFinish;
    }

    @Override
    protected Void doInBackground(String... page) {
        try {
            return DataBase.get().getBoxStore().callInTx(() -> {

                Log.i(TAG, "Parsing " + User.getYear(pos) + User.getPeriod(pos));

                Date today = new Date();

                Box<Matter> matterBox = DataBase.get().getBoxStore().boxFor(Matter.class);
                Box<Period> periodBox = DataBase.get().getBoxStore().boxFor(Period.class);
                Box<Journal> journalBox = DataBase.get().getBoxStore().boxFor(Journal.class);

                Document document = Jsoup.parse(page[0]);

                Elements dates = document.getElementsByTag("option");

                String[] years = new String[dates.size() - 1];

                for (int i = 0; i < dates.size() - 1; i++) {
                    years[i] = dates.get(i + 1).text();
                }

                User.setYears(years);

                Element tableMatters = document.getElementsByTag("tbody").get(12);

                if (!BuildConfig.DEBUG) {
                    Crashlytics.log(Log.ERROR, TAG, tableMatters.toString());
                }

                for (int i = 0; i < tableMatters.select("table.conteudoTexto").size(); i++) {
                    Element nxtElem = null;

                    if (tableMatters.select("table.conteudoTexto").eq(i).parents().eq(0).parents().eq(0).next().eq(0) != null) {
                        nxtElem = tableMatters.select("table.conteudoTexto").eq(i).parents().eq(0).parents().eq(0).next().eq(0).first();
                    }

                    String description = tableMatters.select("table.conteudoTexto").eq(i).parents().eq(0).parents().eq(0).first().child(0).text();

                    Matter matter = matterBox.query()
                            .equal(Matter_.description_, description).and()
                            .equal(Matter_.year_, User.getYear(pos)).and()
                            .equal(Matter_.period_, User.getPeriod(pos))
                            .build().findUnique();

                    if (matter == null) {
                        matter = new Matter(description, pickColor(description), User.getYear(pos), User.getPeriod(pos));
                        matterBox.put(matter);
                    }

                    int periodCount = 0;

                    while (nxtElem != null && nxtElem.child(0).child(0).is("div")) {

                        String periodTitle = nxtElem.child(0).child(0).ownText();
                        Element tableGrades = nxtElem.child(0).child(1).child(0);
                        Elements rowGrades = tableGrades.getElementsByClass("conteudoTexto");
                        nxtElem = nxtElem.nextElementSibling();

                        Period period = null;

                        if (matter.periods.size() - 1 > periodCount) {
                            period = matter.periods.get(periodCount);
                        }

                        if (periodTitle.contains("Exame") || periodTitle.contains("Reavaliação")) {
                            period = matter.periods.get(periodCount - 1);
                        } else {
                            periodCount++;
                            if (period == null) {
                                period = new Period(periodTitle);
                            } else {
                                period.setTitle(periodTitle);
                            }
                        }

                        for (int j = 0; j < rowGrades.size(); j++) {
                            String dateString = formatDate(rowGrades.eq(j).first().child(1).text());
                            String infos = rowGrades.eq(j).first().child(1).text();

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
                            String weightString = formatGrade(formatNumber(rowGrades.eq(j).first().child(2).text()));
                            String maxString = formatGrade(formatNumber(rowGrades.eq(j).first().child(3).text()));
                            String gradeString = formatGrade(formatNumber(rowGrades.eq(j).first().child(4).text()));

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

                                    builder.link(Journal_.matter)
                                            .equal(Matter_.description_, description).and()
                                            .equal(Matter_.year_, User.getYear(pos)).and()
                                            .equal(Matter_.period_, User.getPeriod(pos));

                                    Journal search = builder.build().findUnique();

                                    if (search == null) {

                                        Journal newJournal = new Journal(title, grade, weight, max, date, type, period, matter);

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
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onFinish.onFinish(PG_DIARIOS, pos);
    }

    public interface OnFinish {
        void onFinish(int pg, int year);
    }

    private String formatNumber(String s) {
        return s.substring(s.indexOf(":") + 1).trim();
    }

    private String formatGrade(String s){
        return s.startsWith(",") ? "" : s.replaceAll(",", ".");
    }

    private String formatDescription(String s) {
        return s.substring(0, s.lastIndexOf("-")).trim();
    }

    private String formatDate(String s) {
        return s.substring(0, s.indexOf(',')).trim();
    }

    private String formatType(String s) {
        s = s.substring(s.indexOf(",") + 1);
        return s.substring(0, s.indexOf(':')).trim();
    }

    private String formatJournalTitle(String s) {
        return s.substring(s.indexOf(":") + 1).trim();
    }

    private int getRandomColorGenerator() {
        int color = new Random().nextInt(9);

        switch (color) {
            case 0: color = R.color.deep_orange_500;
                break;

            case 1: color = R.color.yellow_a700;
                break;

            case 2: color = R.color.lime_a700;
                break;

            case 3: color = R.color.light_green_500;
                break;

            case 4: color = R.color.teal_500;
                break;

            case 5: color = R.color.cyan_500;
                break;

            case 6: color = R.color.light_blue_500;
                break;

            case 7: color = R.color.indigo_500;
                break;

            case 8: color = R.color.deep_purple_500;
                break;
        }
        return App.getContext().getResources().getColor(color);
    }

    private int pickColor(String description) {
        int color = 0;

        Matter matter = DataBase.get().getBoxStore().boxFor(Matter.class).query().equal(Matter_.description_, description).build().findFirst();

        if (matter != null) {
            color = matter.getColor();
        }

        if (color == 0) {
            color = getRandomColorGenerator();
        }

        return color;
    }

    private void sendNotification(Journal journal) {
        Intent intent = new Intent(App.getContext(), EventViewActivity.class);
        intent.putExtra("ID", journal.id);
        intent.putExtra("TYPE", CalendarBase.ViewType.JOURNAL);

        Jobs.displayNotification(App.getContext(), journal.getMatter(), journal.getTitle(),
                App.getContext().getResources().getString(R.string.title_diarios), (int) journal.id, intent);
    }

}
