package com.tinf.qmobile.model.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public String getYearString() {
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
    public boolean equals(CalendarBase event) {
        if (event instanceof Month) {
            Month m = (Month) event;

            return m.time == time;
        }

        return false;
    }

}
