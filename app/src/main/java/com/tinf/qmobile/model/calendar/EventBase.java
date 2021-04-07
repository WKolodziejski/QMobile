package com.tinf.qmobile.model.calendar;

import androidx.annotation.ColorInt;

import com.github.sundeepk.compactcalendarview.domain.Event;
import com.tinf.qmobile.App;
import com.tinf.qmobile.R;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.objectbox.annotation.BaseEntity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;

@BaseEntity
public abstract class EventBase extends Event implements CalendarBase {
    @Id public long id;
    private String title;
    private long startTime;
    private long endTime;
    private String description;
    @ColorInt private int color;
    @Transient public boolean isHeader;

    private static SimpleDateFormat day = new SimpleDateFormat("d", Locale.getDefault());
    private static SimpleDateFormat week = new SimpleDateFormat("EE", Locale.getDefault());

    public EventBase(String title, long startTime) {
        super(0, startTime);
        this.title = title;
        this.startTime = startTime;
        endTime = 0;
    }

    public EventBase(String title, long startTime, long endTime) {
        super(0, startTime);
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public EventBase(long startTime, @ColorInt int color) {
        super(color, startTime);
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

    @Override
    public long getTimeInMillis() {
        return startTime;
    }

    @Override
    public boolean isHeader() {
        return isHeader;
    }

    public String getDayString() {
        return day.format(startTime);
    }

    public String getWeekString() {
        return week.format(startTime);
    }

    /*
     * Required methods
     */

    public EventBase() {
        super(0, 0);
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

    @Override
    public int getColor() {
        return color == 0 ? App.getContext().getResources().getColor(R.color.colorAccent) : color;
    }

    @Override
    public boolean equals(CalendarBase event) {
        if (event instanceof EventBase) {
            EventBase e = (EventBase) event;

            boolean eq = e.color == color
                    && e.startTime == startTime
                    && e.endTime == endTime;

            if (e.title != null)
                eq = eq && e.title.equals(title);

            if (e.description != null)
                eq = eq && e.description.equals(description);

            return eq;
        }
        return false;
    }

    @Override
    public LocalDate getHashKey() {
        return new LocalDate(getDate());
    }

}
