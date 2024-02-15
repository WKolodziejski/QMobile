package com.tinf.qmobile.model.calendar;

import static com.tinf.qmobile.model.ViewType.PADDING;

import android.text.format.DateUtils;

import com.tinf.qmobile.model.Queryable;

import org.joda.time.LocalDate;

import java.util.Calendar;
import java.util.Date;

public class Padding implements CalendarBase {

  private final long time;

  public Padding(long time) {
    this.time = time;
  }

  @Override
  public int getItemType() {
    return PADDING;
  }

  @Override
  public long getId() {
    return PADDING;
  }

  @Override
  public boolean isSame(Queryable queryable) {
    return queryable.equals(this);
  }

  @Override
  public Date getDate() {
    return new Date(time);
  }

  @Override
  public int getDay() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(time);
    return calendar.get(Calendar.DAY_OF_YEAR);
  }

  @Override
  public int getYear() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(time);
    return calendar.get(Calendar.YEAR);
  }

  @Override
  public LocalDate getHashKey() {
    return new LocalDate(getDate());
  }

  @Override
  public boolean isHeader() {
    return true;
  }

  @Override
  public boolean isToday() {
    return DateUtils.isToday(getDate().getTime());
  }

  @Override
  public String getDayString() {
    return null;
  }

  @Override
  public String getWeekString() {
    return null;
  }
}
