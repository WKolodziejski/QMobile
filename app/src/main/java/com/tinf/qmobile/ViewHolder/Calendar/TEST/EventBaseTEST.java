package com.tinf.qmobile.ViewHolder.Calendar.TEST;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.Base.CalendarBase;
import com.tinf.qmobile.R;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.ColorInt;
import io.objectbox.annotation.BaseEntity;
import io.objectbox.annotation.Id;

@BaseEntity
public abstract class EventBaseTEST extends Event implements CalendarBase, MultiItemEntity {
    @Id public long id;
    private String title;
    private long startTime;
    private long endTime;
    private String description;
    @ColorInt private int color;

    public EventBaseTEST(String title, long startTime) {
        super(0, startTime);
        this.title = title;
        this.startTime = startTime;
        endTime = 0;
    }

    public EventBaseTEST(String title, long startTime, long endTime) {
        super(0, startTime);
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public EventBaseTEST(long startTime, @ColorInt int color) {
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

            return s.getDay() != e.getDay();
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

    public EventBaseTEST() {
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
