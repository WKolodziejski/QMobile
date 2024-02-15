package com.tinf.qmobile.model.calendar;

import static com.tinf.qmobile.model.ViewType.DAY;

import com.tinf.qmobile.model.Queryable;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Day implements CalendarBase {
  private final Date startDate;
  private final Date endDate;

  private static final SimpleDateFormat year =
      new SimpleDateFormat("MMM, yyyy", Locale.getDefault());
  private static final SimpleDateFormat month = new SimpleDateFormat("MMM", Locale.getDefault());
  private static final SimpleDateFormat day = new SimpleDateFormat("d", Locale.getDefault());

  public Day(Date startDate, Date endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public String getDayPeriod() {
    Calendar start = Calendar.getInstance();
    Calendar end = Calendar.getInstance();
    start.setTime(startDate);
    end.setTime(endDate);

    String period = day.format(startDate);

    if (start.get(Calendar.DAY_OF_YEAR) != end.get(Calendar.DAY_OF_YEAR)) {
      if (start.get(Calendar.DAY_OF_MONTH) > end.get(Calendar.DAY_OF_MONTH))
        period += " " + month.format(startDate) + " - " + day.format(endDate);
      else
        period += " - " + day.format(endDate);
    }

    if (getYear() != new LocalDate().getYear())
      period += " " + year.format(endDate);
    else
      period += " " + month.format(endDate);

    return period;
  }

  @Override
  public boolean isHeader() {
    return true;
  }

  @Override
  public int getItemType() {
    return DAY;
  }

  @Override
  public long getId() {
    return DAY;
  }

  @Override
  public boolean isSame(Queryable queryable) {
    return false;
  }

  @Override
  public Date getDate() {
    return startDate;
  }

  @Override
  public int getDay() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    return calendar.get(Calendar.DAY_OF_YEAR);
  }

  @Override
  public int getYear() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    return calendar.get(Calendar.YEAR);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Day)) return false;
    Day day = (Day) o;
    return startDate.equals(day.startDate) && endDate.equals(day.endDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startDate, endDate);
  }

    /*@Override
    public boolean equals(CalendarBase event) {
        if (event instanceof Day) {
            Day d = (Day) event;
            return d.startDate.equals(startDate) && d.endDate.equals(endDate);
        }
        return false;
    }*/

  @Override
  public LocalDate getHashKey() {
    return new LocalDate(startDate);
  }


}
