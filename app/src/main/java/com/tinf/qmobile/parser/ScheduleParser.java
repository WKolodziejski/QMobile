package com.tinf.qmobile.parser;

import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.tinf.qmobile.App;
import com.tinf.qmobile.BuildConfig;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.utility.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.relation.ToMany;

import static com.tinf.qmobile.network.OnResponse.PG_HORARIO;

public class ScheduleParser extends AsyncTask<String, Void, Boolean> {
    private final static String TAG = "HorarioParser";
    private OnFinish onFinish;
    private int pos;

    public ScheduleParser(int pos, OnFinish onFinish) {
        this.pos = pos;
        this.onFinish = onFinish;
        Log.i(TAG, "New instance");
    }

    @Override
    protected Boolean doInBackground(String... page) {
        try {
            return App.getBox().callInTx(() -> {

                Log.i(TAG, "Parsing " + User.getYear(pos));

                Box<Schedule> scheduleBox = App.getBox().boxFor(Schedule.class);
                Box<Matter> matterBox = App.getBox().boxFor(Matter.class);

                Document document = Jsoup.parse(page[0]);

                Elements tables = document.select("table");

                if (!BuildConfig.DEBUG) {
                    Crashlytics.log(Log.ERROR, TAG, tables.toString());
                }

                Elements scheduleTable = tables.get(11).getElementsByTag("tr");

                if (scheduleTable != null) {

                    List<Matter> matters = matterBox.query()
                            .equal(Matter_.year_, User.getYear(pos)).and()
                            .equal(Matter_.period_, User.getPeriod(pos))
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
                        Elements column = scheduleTable.get(i).select("td");
                        String time = column.get(0).text();

                        for (int j = 1; j < column.size(); j++) {
                            if (!column.get(j).text().isEmpty()) {
                                Schedule schedule = new Schedule(j, getStartHour(time), getStartMinute(time), getEndHour(time), getEndMinute(time), User.getYear(pos), User.getPeriod(pos));

                                String matterTitle = formatTitle(column.get(j).getElementsByTag("div").get(1).attr("title"));
                                //String clazz = formatClass(column.get(j).getElementsByTag("div").get(3).ownText());

                                if (matterTitle != null) {
                                    Matter matter = matterBox.query()
                                            .equal(Matter_.title_, matterTitle).and()
                                            .equal(Matter_.year_, User.getYear(pos)).and()
                                            .equal(Matter_.period_, User.getPeriod(pos))//.and()
                                            //.contains(Matter_.description_, clazz)
                                            .build().findUnique();

                                    if (matter != null) {
                                        String room = column.get(j).getElementsByTag("div").get(2).attr("title");

                                        if (room != null) {
                                            schedule.setRoom(room);
                                        }

                                        schedule.matter.setTarget(matter);
                                        scheduleBox.put(schedule);
                                        matter.schedules.add(schedule);
                                        matterBox.put(matter);
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        onFinish.onFinish(PG_HORARIO, pos);
    }

    public interface OnFinish {
        void onFinish(int pg, int year);
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
