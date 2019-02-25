package com.tinf.qmobile.Class.Materias;

import java.util.Calendar;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

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

    public Calendar getStartTime(int month) {
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.MONTH, month);
        startTime.set(Calendar.DAY_OF_WEEK, day);
        startTime.set(Calendar.HOUR_OF_DAY, getStartHour());
        startTime.set(Calendar.MINUTE, getStartMinute());
        return startTime;
    }

    public Calendar getEndTime(int month) {
        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.MONTH, month);
        endTime.set(Calendar.DAY_OF_WEEK, day);
        endTime.set(Calendar.HOUR_OF_DAY, getEndHour());
        endTime.set(Calendar.MINUTE, getEndMinute());
        return endTime;
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
