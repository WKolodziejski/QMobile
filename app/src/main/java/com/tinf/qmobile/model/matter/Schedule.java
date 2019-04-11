package com.tinf.qmobile.model.matter;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;
import me.jlurena.revolvingweekview.DayTime;

@Entity
public class Schedule {
    @Id public long id;
    private int startDay;
    private int endDay;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private int color;
    private long alarm;
    private String description;
    private String title;
    private boolean isFromSite;
    public ToOne<Matter> matter;

    public Schedule(int startDay, int startHour, int startMinute, int endHour, int endMinute) {
        this.startDay = startDay;
        this.endDay = startDay;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.isFromSite = true;
    }

    public Schedule(String title, int startDay, int endDay, int startHour, int startMinute, int endHour, int endMinute) {
        this.title = title;
        this.startDay = startDay;
        this.endDay = endDay;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public DayTime getStartTime() {
        return new DayTime(startDay, startHour, startMinute);
    }

    public DayTime getEndTime() {
        return new DayTime(endDay, endHour, endMinute);
    }

    /*
     * Required methods
     */

    public Schedule() {}

    public void setDescription(String description) {
        this.description = description;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setAlarm(long alarm) {
        this.alarm = alarm;
    }

    public int getStartDay() {
        return startDay;
    }

    public int getEndDay() {
        return endDay;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public String getDescription() {
        return description;
    }

    public int getColor() {
        return matter.getTargetId() != 0 ? matter.getTarget().getColor() : color;
    }

    public String getTitle() {
        return title;
    }

    public long getAlarm() {
        return alarm;
    }

    public boolean isFromSite() {
        return isFromSite;
    }

}
