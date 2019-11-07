package com.tinf.qmobile.model.calendar.base;

import androidx.annotation.ColorInt;

import com.github.sundeepk.compactcalendarview.domain.Event;
import com.tinf.qmobile.App;
import com.tinf.qmobile.R;

import java.util.Calendar;
import java.util.Date;

import io.objectbox.annotation.BaseEntity;
import io.objectbox.annotation.Id;

@BaseEntity
public abstract class EventBase extends Event implements CalendarBase {
    @Id public long id;
    private String title;
    private long startTime;
    private long endTime;
    private String description;
    @ColorInt private int color;

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

    public String getStartDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    public String getEndDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTime);
        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    public String getMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        return String.valueOf(calendar.get(Calendar.MONTH));
    }

    public boolean isRanged() {
        if (endTime == 0 || endTime == startTime)
            return false;
        else {
            Date s = new Date(startTime);
            Date e = new Date(endTime);

            return !s.equals(e);
        }
    }

    @Override
    public Date getDate() {
        return new Date(startTime);
    }

    @Override
    public long getTimeInMillis() {
        return startTime;
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

    @ColorInt
    @Override
    public int getColor() {
        return color == 0 ? App.getContext().getResources().getColor(R.color.colorAccent) : color;
    }

}
