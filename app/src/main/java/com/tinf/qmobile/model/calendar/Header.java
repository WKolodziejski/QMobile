package com.tinf.qmobile.model.calendar;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.tinf.qmobile.model.ViewType.HEADER;

import com.tinf.qmobile.model.Queryable;

public class Header implements CalendarBase {
    private final long time;

    private static final SimpleDateFormat day = new SimpleDateFormat("d", Locale.getDefault());
    private static final SimpleDateFormat week = new SimpleDateFormat("EE", Locale.getDefault());

    public Header(long time) {
        this.time = time;
    }

    public String getDayString() {
        return day.format(time);
    }

    public String getWeekString() {
        return week.format(time);
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
    public LocalDate getHashKey() {
        return new LocalDate(getDate());
    }

    @Override
    public boolean equals(CalendarBase event) {
        if (event instanceof Header) {
            Header h = (Header) event;

            return h.time == time;
        }
        return false;
    }

    @Override
    public boolean isHeader() {
        return true;
    }

    @Override
    public long getId() {
        return HEADER;
    }

    @Override
    public boolean isSame(Queryable queryable) {
        return queryable.equals(this);
    }

}
