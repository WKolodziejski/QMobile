package com.tinf.qmobile.parser;

import static io.objectbox.query.QueryBuilder.StringOrder.CASE_INSENSITIVE;

import android.util.Log;

import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Schedule;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

import io.objectbox.exception.NonUniqueResultException;
import io.objectbox.relation.ToMany;

public class ScheduleParser extends BaseParser {
    private final static String TAG = "ScheduleParser";

    public ScheduleParser(int page, int year, int period, boolean notify, BaseParser.OnFinish onFinish, OnError onError) {
        super(page, year, period, notify, onFinish, onError);
    }

    @Override
    public void parse(Document document) {
        Log.i(TAG, "Parsing " + year);

        Elements tables = document.select("table");
        Elements scheduleTable = tables.get(11).getElementsByTag("tr");

        if (scheduleTable.isEmpty())
            return;

        List<Matter> matters = matterBox.query()
                .equal(Matter_.year_, year).and()
                .equal(Matter_.period_, period)
                .build().find();

        for (int i = 0; i < matters.size(); i++) {
            ToMany<Schedule> s = matters.get(i).schedules;
            for (int j = 0; j < s.size(); j++) {
                Schedule h = s.get(j);
                if (h.isFromSite()) {
                    scheduleBox.remove(h.id);
                }
            }
        }

        for (int i = 1; i < scheduleTable.size(); i++) {
            Elements row = scheduleTable.get(i).select("td");
            String time = row.get(0).text();

            for (int j = 1; j < row.size(); j++) {
                if (row.get(j).text().isEmpty())
                    continue;

                Elements divs = row.get(j).child(0).child(0).getElementsByTag("div");

                //Log.d("DIVS", divs.toString());

                int k = 0;

                while (k < divs.size()) {

                    Schedule schedule = new Schedule(j, getStartHour(time), getStartMinute(time),
                            getEndHour(time), getEndMinute(time), year, period);

                    String matterTitle = formatTitle(divs.get(k).attr("title"));
                    String room = divs.get(k + 1).attr("title");
                    String clazz = formatClass(divs.get(k + 2).text());

                    k += 3;

                    if (matterTitle.isEmpty())
                        continue;

                    Matter matter = null;

                    try {
                        crashlytics.log(matterTitle);

                        matter = matterBox.query()
                                .contains(Matter_.description_, StringUtils.stripAccents(matterTitle), CASE_INSENSITIVE).and()
                                .equal(Matter_.year_, year).and()
                                .equal(Matter_.period_, period).and()
                                .contains(Matter_.description_, StringUtils.stripAccents(clazz), CASE_INSENSITIVE)
                                .build().findUnique();

                        if (matter == null) {
                            matter = matterBox.query()
                                    .contains(Matter_.description_, StringUtils.stripAccents(matterTitle), CASE_INSENSITIVE).and()
                                    .equal(Matter_.year_, year).and()
                                    .equal(Matter_.period_, period)
                                    .build().findUnique();
                        }
                    } catch (NonUniqueResultException e) {
                        e.printStackTrace();
                    }

                    if (matter == null) {
                        crashlytics.recordException(new Exception(matterTitle + " not found in DB"));
                        Log.d(matterTitle, "Not found in DB");
                        continue;
                    }

                    schedule.setRoom(room);

                    schedule.matter.setTarget(matter);
                    scheduleBox.put(schedule);
                    matter.schedules.add(schedule);
                    matterBox.put(matter);
                }
            }
        }
    }

    private int getStartHour(String time) {
        return formatHour(formatStart(time));
    }

    private int getEndHour(String time) {
        return formatHour(formatEnd(time));
    }

    private int getStartMinute(String time) {
        return formatMinute(formatStart(time));
    }

    private int getEndMinute(String time) {
        return formatMinute(formatEnd(time));
    }

    private int formatHour(String string) {
        string = string.substring(0, string.indexOf(":"));
        return Integer.parseInt(string);
    }

    private int formatMinute(String string) {
        string = string.substring(string.indexOf(":") + 1);
        return Integer.parseInt(string);
    }

    private String formatStart(String string) {
        string = string.substring(0, string.indexOf("~"));
        return string;
    }

    private String formatEnd(String string) {
        string = string.substring(string.indexOf("~") + 1);
        return string;
    }

    private String formatTitle(String s) {
        if (s.contains("(")) {
            s = s.substring(0, s.indexOf("(")).trim();
        }
        return s;
    }

    private String formatClass(String s) {
        if (s.contains(" "))
            s = s.substring(0, s.indexOf(" "));

        return s;
    }

}
