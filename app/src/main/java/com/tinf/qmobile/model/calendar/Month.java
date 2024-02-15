package com.tinf.qmobile.model.calendar;

import static com.tinf.qmobile.model.ViewType.MONTH;

import com.tinf.qmobile.model.Queryable;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Month implements CalendarBase {
  private final long time;

  private static final SimpleDateFormat month =
      new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

  public Month(long time) {
    this.time = time;
  }

  public String getMonth() {
    return month.format(time);
  }

  @Override
  public boolean isHeader() {
    return true;
  }

  @Override
  public boolean isToday() {
    return false;
  }

  @Override
  public String getDayString() {
    return null;
  }

  @Override
  public String getWeekString() {
    return null;
  }

  /*
   * Required methods
   */

  public long getTime() {
    return time;
  }

  @Override
  public int getItemType() {
    return MONTH;
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Month)) return false;
    Month month = (Month) o;
    return getTime() == month.getTime();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTime());
  }

    /*@Override
    public boolean equals(CalendarBase event) {
        if (event instanceof Month) {
            Month m = (Month) event;

            return m.time == time;
        }

        return false;
    }*/

  @Override
  public LocalDate getHashKey() {
    return new LocalDate(getDate());
  }

  @Override
  public long getId() {
    return MONTH;
  }

  @Override
  public boolean isSame(Queryable queryable) {
    return queryable.equals(this);
  }

}
