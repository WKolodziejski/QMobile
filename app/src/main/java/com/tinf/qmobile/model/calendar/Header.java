package com.tinf.qmobile.model.calendar;

import com.tinf.qmobile.model.calendar.base.CalendarBase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.tinf.qmobile.model.calendar.base.CalendarBase.ViewType.HEADER;

public class Header implements CalendarBase {
    private long time;

    public Header(long time) {
        this.time = time;
    }

    public String getDayString() {
        return new SimpleDateFormat("d", Locale.getDefault()).format(time);
    }

    public String getWeekString() {
        return new SimpleDateFormat("EE", Locale.getDefault()).format(time);
    }

    @Override
    public int getItemType() {
        return HEADER;
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
        if (event instanceof Header) {
            Header h = (Header) event;

            return h.time == time;
        }
        return false;
    }

}
