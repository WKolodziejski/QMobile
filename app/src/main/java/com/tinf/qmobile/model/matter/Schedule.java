package com.tinf.qmobile.model.matter;

import androidx.annotation.ColorInt;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;
import me.jlurena.revolvingweekview.DayTime;

@Entity
public class Schedule {
    @Id public long id;
    private int startDay_;
    private int endDay_;
    private int startHour_;
    private int startMinute_;
    private int endHour_;
    private int endMinute_;
    @ColorInt private int color_;
    private long alarm_;
    private String description_;
    private String title_;
    private String room_;
    private boolean isFromSite_;
    public ToOne<Matter> matter;

    public Schedule(int startDay, int startHour, int startMinute, int endHour, int endMinute) {
        this.startDay_ = startDay;
        this.endDay_ = startDay;
        this.startHour_ = startHour;
        this.startMinute_ = startMinute;
        this.endHour_ = endHour;
        this.endMinute_ = endMinute;
        this.isFromSite_ = true;
    }

    public Schedule(String title, int startDay, int endDay, int startHour, int startMinute, int endHour, int endMinute) {
        this.title_ = title;
        this.startDay_ = startDay;
        this.endDay_ = endDay;
        this.startHour_ = startHour;
        this.startMinute_ = startMinute;
        this.endHour_ = endHour;
        this.endMinute_ = endMinute;
    }

    public DayTime getStartTime() {
        return new DayTime(startDay_, startHour_, startMinute_);
    }

    public DayTime getEndTime() {
        return new DayTime(endDay_, endHour_, endMinute_);
    }

    @ColorInt
    public int getColor() {
        return matter.getTargetId() != 0 ? matter.getTarget().getColor() : color_;
    }

    public void setDescription(String description) {
        this.description_ = description;
    }

    public void setColor(int color) {
        this.color_ = color;
    }

    public void setAlarm(long alarm) {
        this.alarm_ = alarm;
    }

    public int getStartDay() {
        return startDay_;
    }

    public int getEndDay() {
        return endDay_;
    }

    public int getStartHour() {
        return startHour_;
    }

    public int getStartMinute() {
        return startMinute_;
    }

    public int getEndHour() {
        return endHour_;
    }

    public int getEndMinute() {
        return endMinute_;
    }

    public String getDescription() {
        return description_;
    }

    public String getTitle() {
        return title_;
    }

    public String getRoom() {
        return room_ == null ? "" : room_;
    }

    public void setRoom(String room) {
        this.room_ = room;
    }

    public long getAlarm() {
        return alarm_;
    }

    public boolean isFromSite() {
        return isFromSite_;
    }

    /*
     * Required methods
     */

    public Schedule() {}

    public int getStartDay_() {
        return startDay_;
    }

    public int getEndDay_() {
        return endDay_;
    }

    public int getStartHour_() {
        return startHour_;
    }

    public int getStartMinute_() {
        return startMinute_;
    }

    public int getEndHour_() {
        return endHour_;
    }

    public int getEndMinute_() {
        return endMinute_;
    }

    public int getColor_() {
        return color_;
    }

    public long getAlarm_() {
        return alarm_;
    }

    public String getDescription_() {
        return description_;
    }

    public String getTitle_() {
        return title_;
    }

    public String getRoom_() {
        return room_;
    }

    public boolean isFromSite_() {
        return isFromSite_;
    }

}
