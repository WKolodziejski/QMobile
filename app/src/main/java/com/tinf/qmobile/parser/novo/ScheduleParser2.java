package com.tinf.qmobile.parser.novo;

import android.os.AsyncTask;
import android.util.Log;

import com.tinf.qmobile.App;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.utility.User;
import com.tinf.qmobile.utility.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.relation.ToMany;

import static com.tinf.qmobile.network.OnResponse.PG_HORARIO;

public class ScheduleParser2 extends AsyncTask<String, Void, Void> {
    private final static String TAG = "HorarioParser";
    private OnFinish onFinish;
    private int pos;

    public ScheduleParser2(int pos, OnFinish onFinish) {
        this.pos = pos;
        this.onFinish = onFinish;

        Log.i(TAG, "New instance");
    }

    @Override
    protected Void doInBackground(String... page) {
        App.getBox().runInTx(() -> {

            Log.i(TAG, "Parsing " + User.getYear(pos));

            Box<Schedule> scheduleBox = App.getBox().boxFor(Schedule.class);
            Box<Matter> matterBox = App.getBox().boxFor(Matter.class);

            Document document = Jsoup.parse(page[0]);

            Elements tables = document.select("table");
            Elements scheduleTable = null, codeTable = null, roomTable = null;

            for (int i = 0; i < tables.size(); i++) {
                String header = tables.get(i).getElementsByTag("tr").get(0).text();
                if (header.contains("HORÁRIO")) {
                    scheduleTable = tables.get(i).getElementsByTag("tr");

                } else if (header.contains("Legenda")) {
                    codeTable = tables.get(i).getElementsByTag("tr");

                } else if (header.contains("MAPA")) {
                    roomTable = tables.get(i).getElementsByTag("tr");
                }
            }

            if (scheduleTable != null && codeTable != null) {

                List<Matter> matters = matterBox.query()
                        .equal(Matter_.year_, User.getYear(pos)).and()
                        .equal(Matter_.period_, User.getPeriod(pos))
                        .build().find();

                for (int i = 0; i < matters.size(); i++) {
                    for (int j = 0; j < matters.get(i).schedules.size(); j++) {
                        ToMany<Schedule> s = matters.get(i).schedules;
                        Schedule h = s.get(j);
                        if (h.isFromSite()) {
                            s.removeById(h.id);
                            scheduleBox.remove(h.id);
                        }
                    }
                    //matters.get(i).schedules.clear();
                }

                HashMap<String, String> codes = new HashMap<>();

                for (int i = 1; i < codeTable.size(); i++) {
                    String c = formatCode(codeTable.get(i).text());
                    String t = formatTitle(codeTable.get(i).text());

                    if (!c.isEmpty() && !t.isEmpty()) {
                        codes.put(c, t);
                    }
                }

                HashMap<String, String> rooms = new HashMap<>();

                if (roomTable != null) {
                    for (int i = 2; i < roomTable.size(); i++) {
                        Elements column = roomTable.get(i).select("td");

                        String c = column.get(0).text();
                        String d = column.get(1).text();

                        if (!c.isEmpty() && !d.isEmpty()) {
                            rooms.put(c, d);
                        }
                    }
                }

                for (int i = 1; i < scheduleTable.size(); i++) {
                    Elements column = scheduleTable.get(i).select("td");
                    String time = column.get(0).text();

                    for (int j = 1; j < column.size(); j++) {
                        if (!column.get(j).text().isEmpty()) {
                            Schedule schedule = new Schedule(j, getStartHour(time), getStartMinute(time), getEndHour(time), getEndMinute(time));
                            String c = formatCode2(column.get(j).text());
                            String matterTitle = codes.get(c);

                            if (matterTitle != null) {
                                Matter matter = matterBox.query()
                                        .equal(Matter_.title_, matterTitle).and()
                                        .equal(Matter_.year_, User.getYear(pos)).and()
                                        .equal(Matter_.period_, User.getPeriod(pos))
                                        .build().findUnique();

                                if (matter != null) {
                                    String room = rooms.get(formatRoom(column.get(j).text()));

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
        });
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
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
            s = s.substring(s.indexOf("-") + 1, s.indexOf("("));
        } else {
            s = s.substring(s.indexOf("-") + 1, s.lastIndexOf("-"));
        }
        s = s.substring(s.indexOf("-") + 1).trim();
        return s;
    }

    private String formatCode(String s) {
        return s.substring(0, s.indexOf("-") - 1).trim();
    }

    private String formatCode2(String s) {
        return s.substring(0, s.indexOf(" ")).trim();
    }

    private String formatRoom(String s) {
        s = s.substring(s.indexOf(" ") + 1);
        s = s.substring(0, s.indexOf(" ")).trim();
        return s;
    }

}
