package com.tinf.qmobile.parser;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.tinf.qmobile.BuildConfig;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.objectbox.Box;
import io.objectbox.exception.NonUniqueResultException;

public class ReportParser extends BaseParser {
    private final static String TAG = "ReportParser";

    public ReportParser(int page, int pos, boolean notify, BaseParser.OnFinish onFinish, OnError onError) {
        super(page, pos, notify, onFinish, onError);
    }

    @Override
    public void parse(Document document) {
        Log.i(TAG, "Parsing " + User.getYear(pos));

        Elements tables = document.getElementsByTag("tbody");

        if (!BuildConfig.DEBUG) {
            Crashlytics.log(Log.ERROR, TAG, tables.toString());
        }

        for (int k = 0; k < tables.size(); k++) {
            if (tables.get(k).getElementsByTag("tr").get(0).text().contains("Estrutura")) {

                if (tables.get(0).childNodeSize() > 1) {
                    Elements rows = tables.get(k).getElementsByTag("tr");

                    for (int i = 2; i < tables.get(k).childNodeSize() - 1; i++) {

                        String matterTitle = formatTitle(rows.get(i).child(0).text());
                        String clazz = formatClass(rows.get(i).child(2).text());
                        String absencesTotal = formatNumber(rows.get(i).child(3).text());
                        String finalMean = formatNumber(rows.get(i).child(4).text());
                        String situation = rows.get(i).child(rows.get(1).children().size() - 1).text();
                        String qid = formatQID(rows.get(i).child(0).getElementsByTag("q_latente").attr("value"));

                        Matter matter = null;

                        try {
                            matter = matterBox.query()
                                    .contains(Matter_.description_, matterTitle).and()
                                    .equal(Matter_.year_, User.getYear(pos)).and()
                                    .equal(Matter_.period_, User.getPeriod(pos)).and()
                                    .contains(Matter_.description_, clazz).and()
                                    .contains(Matter_.description_, qid)
                                    .build().findUnique();
                        } catch (NonUniqueResultException e) {
                            e.printStackTrace();
                        }

                        if (matter != null) {
                            matter.setAbsences(absencesTotal.isEmpty() ? -1 : Integer.parseInt(absencesTotal));
                            matter.setMean(finalMean.isEmpty() ? -1 : Float.parseFloat(finalMean));
                            matter.setTitle(matterTitle);
                            matter.setSituation(situation);

                            for (int j = 0; j < matter.periods.size(); j++) {
                                Period period = matter.periods.get(j);

                                for (int l = 5; l < rows.get(1).children().size(); l++) {
                                    Element h = rows.get(1).child(l);

                                    if (h.children().size() > 0) {
                                        if (h.child(0).attr("title").equals(period.getTitle_())) {
                                            String grade_ = formatNumber(rows.get(i).child(l).text());
                                            String absences_ = formatNumber(rows.get(i).child(++l).text());

                                            period.setGrade(grade_.isEmpty() ? -1 : Float.parseFloat(grade_));
                                            period.setAbsences(absences_.isEmpty() ? -1 : Integer.parseInt(absences_));

                                            periodBox.put(period);

                                            if (period.isSub_() && ((j + 1) % 2 == 0)) {
                                                period = matter.periods.get(j - 1);

                                                String gradeFinal_ = formatNumber(rows.get(i).child(++l).text());
                                                float gradeFinal = gradeFinal_.isEmpty() ? -1 : Float.parseFloat(gradeFinal_);

                                                period.setGradeFinal(gradeFinal);

                                                periodBox.put(period);
                                            }
                                        }
                                    }
                                }
                            }
                            matterBox.put(matter);
                        }
                    }
                }
            }
        }
    }

    private String formatTitle(String s) {
        if (s.contains("- G")) {
            s = s.substring(0, s.indexOf("- G"));
        }
        if (s.contains("(")) {
            s = s.substring(0, s.indexOf("("));
        }
        return s.trim();
    }

    private String formatClass(String s) {
        return s.substring(s.indexOf("-") + 1).trim();
    }

    private String formatQID(String s) {
        return s.substring(s.indexOf("=") + 1).trim();
    }

    @Override
    protected String formatNumber(String s){
        return s.startsWith(",") ? "" : s.replaceAll(",", ".").trim();
    }

}
