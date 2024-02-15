package com.tinf.qmobile.model.calendar;

import android.text.format.DateUtils;

import androidx.annotation.ColorInt;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.objectbox.annotation.BaseEntity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;

@BaseEntity
public abstract class Event implements CalendarBase {
  @Id
  public long id;
  private String title;
  private long startTime;
  private long endTime;
  private String description;
  private int color;

  @Transient
  private boolean isHeader;

  private static final SimpleDateFormat day = new SimpleDateFormat("d", Locale.getDefault());
  private static final SimpleDateFormat week = new SimpleDateFormat("EE", Locale.getDefault());

  public Event(String title,
               long startTime) {
    this.title = title;
    this.startTime = startTime;
    endTime = 0;
  }

  public Event(String title,
               long startTime,
               long endTime) {
    this.title = title;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public Event(long startTime,
               @ColorInt
                   int color) {
    this.startTime = startTime;
    this.color = color;
    endTime = 0;
  }

  public String getEndDateString() {
    return new SimpleDateFormat("EE, dd/MM/yyyy", Locale.getDefault()).format(endTime);
  }

  public String getStartDateString() {
    return new SimpleDateFormat("EE, dd/MM/yyyy", Locale.getDefault()).format(startTime);
  }

  public String getMonthHashKey() {
    return new SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(startTime);
  }

  public boolean isRanged() {
    if (endTime == 0 || endTime == startTime)
      return false;
    else {
      Calendar s = Calendar.getInstance();
      s.setTimeInMillis(startTime);

      Calendar e = Calendar.getInstance();
      e.setTimeInMillis(endTime);

      return !(s.get(Calendar.DAY_OF_MONTH) == e.get(Calendar.DAY_OF_MONTH)
               && s.get(Calendar.MONTH) == e.get(Calendar.MONTH)
               && s.get(Calendar.YEAR) == e.get(Calendar.YEAR));
    }
  }

  @Override
  public Date getDate() {
    return new Date(startTime);
  }

  @Override
  public int getDay() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(startTime);
    return calendar.get(Calendar.DAY_OF_YEAR);
  }

  @Override
  public int getYear() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(startTime);
    return calendar.get(Calendar.YEAR);
  }

  public long getTimeInMillis() {
    return startTime;
  }

  @Override
  public boolean isHeader() {
    return isHeader;
  }

  @Override
  public boolean isToday() {
//    Calendar now = Calendar.getInstance();
//    Calendar cdate = Calendar.getInstance();
//    cdate.setTimeInMillis(startTime);
//
//    Log.d("TODAY", now.toString());
//    Log.d("DATE", cdate.toString());
//
//    return now.get(Calendar.YEAR) == cdate.get(Calendar.YEAR)
//           && now.get(Calendar.MONTH) == cdate.get(Calendar.MONTH)
//           && now.get(Calendar.DAY_OF_MONTH) == cdate.get(Calendar.DAY_OF_MONTH);

    return DateUtils.isToday(getTimeInMillis());
  }

  @Override
  public String getDayString() {
    return day.format(startTime);
  }

  @Override
  public String getWeekString() {
    return week.format(startTime);
  }

  public void setHeader(boolean header) {
    isHeader = header;
  }

  /*
   * Required methods
   */

  public Event() {
  }

  public void setColor(int color) {
    this.color = color;
  }

  public String getTitle() {
    return title;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getColor() {
    return color == 0 ? App.getContext()
                           .getColor(R.color.colorAccent) : color;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getId(), getTitle(), getStartTime(), getEndTime(),
                        getDescription(), getColor());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Event)) return false;
    if (!super.equals(o)) return false;
    Event event = (Event) o;
    boolean eq = getId() == event.getId() && getStartTime() == event.getStartTime()
                 && getEndTime() == event.getEndTime() && getColor() == event.getColor();

    if (getTitle() != null)
      eq &= getTitle().equals(event.getTitle());

    if (getDescription() != null)
      eq &= getDescription().equals(event.getDescription());

    return eq;
  }

    /*@Override
    public boolean equals(CalendarBase event) {
        //if (this == event) return true;
        if (!(event instanceof EventBase)) return false;
        //if (!super.equals(event)) return false;

        EventBase e = (EventBase) event;

        boolean eq = e.getColor() == getColor()
                && e.getStartTime() == getStartTime()
                && e.getEndTime() == getEndTime();

        if (e.getTitle() != null)
            eq = eq && e.getTitle().equals(getTitle());

        if (e.getDescription() != null)
            eq = eq && e.getDescription().equals(getDescription());

        return eq;
    }*/

  @Override
  public LocalDate getHashKey() {
    return new LocalDate(getDate()).toDateTimeAtStartOfDay().toLocalDate();
  }

}
