package com.tinf.qmobile.model.matter;

import androidx.annotation.ColorInt;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;
import me.jlurena.revolvingweekview.DayTime;

@Entity
public class Schedule {
  @Id
  public long id;
  private int startDay_;
  private int startHour_;
  private int startMinute_;
  private int endHour_;
  private int endMinute_;
  @ColorInt
  private int color_;
  private long alarm_;
  private String title_;
  private String description_;
  private String room_;
  private boolean isFromSite_;
  public ToOne<Matter> matter;
  private int difference;
  private int year;
  private int period;

  public Schedule(int startDay,
                  int startHour,
                  int startMinute,
                  int endHour,
                  int endMinute,
                  int year,
                  int period) {
    this.startDay_ = startDay;
    this.startHour_ = startHour;
    this.startMinute_ = startMinute;
    this.endHour_ = endHour;
    this.endMinute_ = endMinute;
    this.year = year;
    this.period = period;
    this.isFromSite_ = true;
  }

  public Schedule(String title,
                  DayTime start,
                  DayTime end,
                  int difference,
                  int year,
                  int period,
                  boolean isFromSite) {
    this.title_ = title;
    this.startDay_ = start.getDayValue();
    this.startHour_ = start.getHour();
    this.startMinute_ = start.getMinute();
    this.endHour_ = end.getHour();
    this.endMinute_ = end.getMinute();
    this.difference = difference;
    this.year = year;
    this.period = period;
    this.isFromSite_ = isFromSite;
  }

  public DayTime getStartTime() {
    return new DayTime(startDay_, startHour_, startMinute_);
  }

  public DayTime getEndTime() {
    return new DayTime(startDay_, endHour_, endMinute_);
  }

  @ColorInt
  public int getColor() {
    return !matter.isNull() ? matter.getTarget()
                                    .getColor() : color_;
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
    return description_ == null ? "" : description_;
  }

  public String getTitle() {
    return title_ == null ? matter.getTarget()
                                  .getTitle() : title_;
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

  public int getDifference() {
    return difference;
  }

  public String getMatter() {
    return isFromSite_ || matter.isNull() ? "" : matter.getTarget()
                                                       .getTitle();
  }

  public void setFromSite(boolean fromSite) {
    isFromSite_ = fromSite;
  }

  /*
   * Required methods
   */

  public Schedule() {}

  public int getStartDay_() {
    return startDay_;
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

  public int getYear() {
    return year;
  }

  public int getPeriod() {
    return period;
  }

}
