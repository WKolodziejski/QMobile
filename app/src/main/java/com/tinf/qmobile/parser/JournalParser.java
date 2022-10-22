package com.tinf.qmobile.parser;

import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static io.objectbox.query.QueryBuilder.StringOrder.CASE_INSENSITIVE;

import android.content.Intent;
import android.util.Log;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.journal.Journal_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.model.matter.Period_;
import com.tinf.qmobile.utility.NotificationUtils;
import com.tinf.qmobile.utility.RandomColor;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.objectbox.query.QueryBuilder;

public class JournalParser extends BaseParser {

    public JournalParser(int page, int year, int period, boolean notify, OnFinish onFinish, OnError onError) {
        super(page, year, period, notify, onFinish, onError);
    }

    @Override
    public void parse(Document document) {

        RandomColor colors = new RandomColor();

        //Date today = new Date();

        /*Element frm = document.getElementById("ANO_PERIODO2");

        if (frm != null) {
            Elements dates = frm.getElementsByTag("option");

            if (dates != null) {
                String[] years = new String[dates.size() - 1];

                for (int i = 0; i < dates.size() - 1; i++)
                    years[i] = dates.get(i + 1).text();

                User.setYears(years);
            }
        }*/

        Element body = document.getElementsByTag("tbody").get(12);
        Elements contents = body.select("table.conteudoTexto");

        for (int i = 0; i < contents.size(); i++) {
            Elements header = contents.eq(i).parents().eq(0).parents().eq(0);

            Element reports = header.first().child(1);
            Elements trs = reports.getElementsByTag("tr");

            String description = header.first().child(0).text();
            String teacher = description.substring(description.lastIndexOf('-') + 1).trim();

//            String cTotal = trs.get(1).getElementsByTag("td").get(1).text().trim();
//            String cLeft = trs.get(3).getElementsByTag("td").get(1).text().trim();
//            String abs = trs.get(4).getElementsByTag("td").get(1).text().trim();
//            String hs = trs.get(0).getElementsByTag("td").get(1).text().trim();
//            hs = hs.substring(0, hs.indexOf("Hrs")).trim();
//            String cGiven = trs.get(2).getElementsByTag("td").get(1).text().trim();

            Log.d("trs", trs.toString());

            String cTotal = formatHeader(trs, HeaderType.CLASSES_TOTAL);
            //String cLeft = formatHeader(trs, HeaderType.CLASSES_LEFT);
            String cGiven = formatHeader(trs, HeaderType.CLASSES_GIVEN);
            String abs = formatHeader(trs, HeaderType.ABSENCES);
            String hs = formatHeader(trs, HeaderType.HOURS);
            hs = hs.substring(0, hs.indexOf("Hrs")).trim();

            if (cGiven.contains("[")) {
                cGiven = cGiven.substring(0, cGiven.indexOf(" ")).trim();
            }

            float hours = hs.isEmpty() ? -1 : Float.parseFloat(hs);
            int classesTotal = cTotal.isEmpty() ? -1 : Integer.parseInt(cTotal);
            int classesGiven = cGiven.isEmpty() ? -1 : Integer.parseInt(cGiven);
            int absences = abs.isEmpty() ? -1 : Integer.parseInt(abs);

            Log.d(description, hours + ", " + classesTotal + ", " + classesGiven);

            boolean isFirstParse = false;
            Matter matter = null;

            try {
                matter = matterBox.query()
                        .equal(Matter_.description_, StringUtils.stripAccents(description), CASE_INSENSITIVE).and()
                        .equal(Matter_.year_, year).and()
                        .equal(Matter_.period_, period)
                        .build().findUnique();
            } catch (Exception e) {
                Log.e("JournalParser", description);
                e.printStackTrace();
            }

            if (matter == null) {
                Log.d(description, "Query failed");

                List<Matter> matters = matterBox.query()
                        .equal(Matter_.year_, year).and()
                        .equal(Matter_.period_, period)
                        .build().find();

                for (Matter m : matters)
                    if (StringUtils.containsIgnoreCase(m.getDescription_(), StringUtils.stripAccents(description)))
                        matter = m;
            }

            if (matter == null) {
                matter = new Matter(StringUtils.stripAccents(description), colors.getColor(), hours, classesTotal, year, period);
                isFirstParse = true;
            }

            if (!teacher.isEmpty())
                matter.setTeacher(teacher);

            matter.setClassesTotal(classesTotal);
            matter.setClassesGiven(classesGiven);
            matter.setAbsences(absences);

            Element content = null;

            if (!header.next().eq(0).isEmpty())
                content = header.next().eq(0).first();

            int periodCount = 0;

            while (content != null && content.child(0).childrenSize() > 0 && content.child(0).child(0).is("div")) {
                String periodTitle = content.child(0).child(0).ownText();
                Element tableGrades = content.child(0).child(1).child(0);
                Elements grades = tableGrades.getElementsByClass("conteudoTexto");
                content = content.nextElementSibling();

                Period period;

                if (periodCount <= matter.periods.size() - 1) {
                    period = matter.periods.get(periodCount);
                } else {
                    period = new Period(periodTitle);
                    if (periodTitle.contains("Exame") || periodTitle.contains("Reavalia") || periodTitle.contains("Final") || periodTitle.contains("Conceito")) {
                        period.setSub();
                    }
                }

                periodCount++;

                for (int j = 0; j < grades.size(); j++) {
                    String dateString = formatDate(grades.eq(j).first().child(1).text());
                    String infos = grades.eq(j).first().child(1).text();
                    String t = formatType(infos);
                    String title = formatJournalTitle(infos);
//                    String weightString = formatJournal(grades.eq(j).first(), HeaderType.WEIGHT);
//                    String maxString = formatJournal(grades.eq(j).first(), HeaderType.MAX);
//                    String gradeString = formatJournal(grades.eq(j).first(), HeaderType.GRADE);

                    String weightString = formatGrade(formatNumber(grades.eq(j).first().child(2).text()));
                    String maxString;
                    String gradeString;

                    if (grades.eq(j).first().child(3).text().contains(HeaderType.MAX.txt)) {
                        maxString = grades.eq(j).first().child(3).text();
                        maxString = formatGrade(formatNumber(maxString.substring(maxString.indexOf(" ")).trim()));
                        gradeString = formatGrade(formatNumber(grades.eq(j).first().child(4).text()));
                    } else {
                        maxString = "";
                        gradeString = formatGrade(formatNumber(grades.eq(j).first().child(3).text()));
                    }

//                    String weightString = formatGrade(formatNumber(grades.eq(j).first().child(2).text()));
//                    String maxString = formatGrade(formatNumber(grades.eq(j).first().child(3).text()));
//                    String gradeString = formatGrade(formatNumber(grades.eq(j).first().child(4).text()));

                    float grade = gradeString.isEmpty() ? -1 : Float.parseFloat(gradeString);
                    float weight = weightString.isEmpty() ? -1 : Float.parseFloat(weightString);
                    float max = maxString.isEmpty() ? -1 : Float.parseFloat(maxString);
                    long date = dateString.isEmpty() ? -1 : getDate(dateString);
                    int type = Journal.Type.AVALIACAO.get();

                    if (t.equals("Prova")) {
                        type = Journal.Type.PROVA.get();
                    } else if (t.equals("Diarios") || t.equals("Trabalho")) {
                        type = Journal.Type.TRABALHO.get();
                    } else if (t.contains("Qualitativa")) {
                        type = Journal.Type.QUALITATIVA.get();
                    } else if (t.contains("Exerc")) {
                        type = Journal.Type.EXERCICIO.get();
                    }

                    if (date == -1)
                        continue;

                    Journal search = null;

                    try {
                        QueryBuilder<Journal> builder = journalBox.query()
                                .equal(Journal_.title, title, CASE_INSENSITIVE).and()
                                .equal(Journal_.type_, type).and()
                                .between(Journal_.startTime, date, date).and()
                                .between(Journal_.weight_, weight, weight).and()
                                .between(Journal_.max_, max, max);

                        builder.link(Journal_.period)
                                .equal(Period_.id, period.id);

                        search = builder.build().findUnique();
                    } catch (Exception e) {
                        Log.e("JournalParser", title);
                        e.printStackTrace();
                        crashlytics.recordException(e);
                    }

                    if (search == null) {
                        Journal newJournal = new Journal(title, grade, weight, max, date, type, period, matter, isFirstParse);

                        period.journals.add(newJournal);
                        journalBox.put(newJournal);

                        if (notify) {
                            if (grade != -1 && date <= new Date().getTime())
                                notifyGrade(newJournal);
                            else
                                notifySchedule(newJournal);
                        }
                    } else {
                        if (search.getGrade_() != grade) {
                            search.setGrade(grade);
                            journalBox.put(search);

                            if (notify) {
                                notifyGrade(search);
                            }
                        }
                    }
                }

                period.matter.setTarget(matter);
                matter.periods.add(period);
                periodBox.put(period);
            }

            matterBox.put(matter);

            Log.d(description, matter.toString());
        }
    }

    private void notifyGrade(Journal journal) {
        Intent intent = new Intent(App.getContext(), EventViewActivity.class);
        intent.putExtra("ID", journal.id);
        intent.putExtra("TYPE", JOURNAL);

        NotificationUtils.show(
                String.format(Locale.getDefault(),
                        App.getContext().getResources().getString(R.string.notification_journal_title),
                        journal.getType(),
                        journal.matter.getTarget().getTitle()),
                journal.getTitle(),
                JOURNAL, (int) journal.id, intent);
    }

    private void notifySchedule(Journal journal) {
        Intent intent = new Intent(App.getContext(), EventViewActivity.class);
        intent.putExtra("ID", journal.id);
        intent.putExtra("TYPE", JOURNAL);

        NotificationUtils.show(
                String.format(Locale.getDefault(),
                        App.getContext().getResources().getString(R.string.notification_journal_title),
                        journal.getType(),
                        journal.matter.getTarget().getTitle()),
                journal.formatDate(),
                JOURNAL, (int) journal.id, intent);
    }

    private String formatType(String s) {
        s = s.substring(s.indexOf(",") + 1);
        return s.substring(0, s.indexOf(':')).trim();
    }

    private String formatJournalTitle(String s) {
        if (s.contains(","))
            s = s.substring(s.indexOf(",") + 1);

        return s.substring(s.indexOf(":") + 1).trim();
    }

    private String formatDate(String s) {
        return s.substring(0, s.indexOf(',')).trim();
    }

    private long getDate(String s) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.YEAR, Integer.parseInt(s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf("/") + 5)));
        cal.set(Calendar.MONTH, Integer.parseInt(s.substring(s.indexOf("/") + 1, s.lastIndexOf("/"))) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(0, s.indexOf("/"))));

        return cal.getTimeInMillis();
    }

    private String formatHeader(Elements trs, HeaderType field) {
        for (int i = 0; i < trs.size(); i++) {
            Elements tds = trs.get(i).getElementsByTag("td");
            String title = tds.get(0).text();
            String value = tds.get(1).text();

            if (title.contains(field.txt)) {
                return value;
            }
        }

        return "";
    }

//    private String formatJournal(Element e, HeaderType field) {
//        for (int i = 0; i < e.childrenSize(); i++) {
//            String txt = e.child(i).text();
//
//            if (txt.contains(field.txt)) {
//                return formatGrade(formatNumber(txt.substring(txt.indexOf(field.txt) + 1).trim()));
//            }
//        }
//
//        return "";
//    }

    private String formatGrade(String s) {
        return s.startsWith(",") ? "" : s.replaceAll(",", ".").trim();
    }

    private String formatNumber(String s) {
        if (s.contains(" "))
            return s.substring(s.lastIndexOf(" ")).trim();

        return "";
        //return s.substring(s.indexOf(":") + 1).trim();
    }

    private enum HeaderType {
        CLASSES_TOTAL("previsto"),
        CLASSES_GIVEN("ministradas"),
        ABSENCES("Faltas"),
        HOURS("prevista"),
        WEIGHT("Peso"),
        MAX("xima"),
        GRADE("Nota");

        HeaderType(String txt) {
            this.txt = txt;
        }

        public String txt;
    }

}
