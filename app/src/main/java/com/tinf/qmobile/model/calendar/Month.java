package com.tinf.qmobile.model.calendar;

import com.tinf.qmobile.model.calendar.base.CalendarBase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Month implements CalendarBase {
    @Id public long id;
    private long time;

    public Month(long time) {
        this.time = time;
    }

    public String getMonth() {
        return new SimpleDateFormat("MMMM", Locale.getDefault()).format(time);
    }

    public String getYear() {
        return new SimpleDateFormat("yyyy", Locale.getDefault()).format(time);
    }

    /*
     * Required methods
     */

    public Month() {}

    public long getTime() {
        return time;
    }

    @Override
    public int getItemType() {
        return CalendarBase.ViewType.MONTH;
    }

    @Override
    public Date getDate() {
        return new Date(time);
    }

}