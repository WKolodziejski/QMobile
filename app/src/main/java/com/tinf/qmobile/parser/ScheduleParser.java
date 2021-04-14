package com.tinf.qmobile.parser;

import android.util.Log;

import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Schedule;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

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

        if (scheduleTable != null) {

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
                    if (!row.get(j).text().isEmpty()) {

                        Elements divs = row.get(j).child(0).child(0).getElementsByTag("div");

                        //Log.d("DIVS", divs.toString());

                        int k = 0;

                        while (k < divs.size()) {

                            Schedule schedule = new Schedule(j, getStartHour(time), getStartMinute(time),
                                    getEndHour(time), getEndMinute(time), year, period);

                            String matterTitle = formatTitle(divs.get(k).attr("title"));

                            if (matterTitle != null) {
                                Matter matter = matterBox.query()
                                        .equal(Matter_.title_, matterTitle).and()
                                        .equal(Matter_.year_, year).and()
                                        .equal(Matter_.period_, period)//.and()
                                        //.contains(Matter_.description_, clazz)
                                        .build().findUnique();

                                if (matter != null) {
                                    String room = divs.get(k + 1).attr("title");

                                    if (room != null) {
                                        schedule.setRoom(room);
                                    }

                                    schedule.matter.setTarget(matter);
                                    scheduleBox.put(schedule);
                                    matter.schedules.add(schedule);
                                    matterBox.put(matter);
                                }
                            }

                            k += 3;
                        }
                    }
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
        return Integer.valueOf(string);
    }

    private int formatMinute(String string) {
        string = string.substring(string.indexOf(":") + 1);
        return Integer.valueOf(string);
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
        if (s.contains("- G")) {
            s = s.substring(0, s.indexOf("- G")).trim();
        }
        return s;
    }

}
