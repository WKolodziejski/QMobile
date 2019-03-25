package com.tinf.qmobile.Class.Materias;

import java.util.Calendar;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;
import me.jlurena.revolvingweekview.DayTime;

@Entity
public class Schedule {
    @Id public long id;
    private int day;
    private String time;
    private boolean isFromSite;
    public ToOne<Matter> matter;

    public Schedule(int day, String time, boolean isFromSite) {
        this.day = day;
        this.time = time;
        this.isFromSite = isFromSite;
    }

    public DayTime getStartTime() {
        return new DayTime(day - 1, getStartHour(), getStartMinute());
    }

    public DayTime getEndTime() {
        return new DayTime(day - 1, getEndHour(), getEndMinute());
    }

    private int getStartHour() {
        return formatHour(formatStart(time));
    }

    private int getEndHour() {
        return formatHour(formatEnd(time));
    }

    private int getStartMinute() {
        return formatMinute(formatStart(time));
    }

    private int getEndMinute() {
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

    /*
     * Required methods
     */

    public Schedule() {}

    public int getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public boolean isFromSite() {
        return isFromSite;
    }

}
