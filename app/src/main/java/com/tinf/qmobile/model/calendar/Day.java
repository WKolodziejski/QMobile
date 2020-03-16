package com.tinf.qmobile.model.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Day implements CalendarBase {
    private Date startDate;
    private Date endDate;

    public Day(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getDayPeriod() {
        SimpleDateFormat month = new SimpleDateFormat("MMM", Locale.getDefault());
        SimpleDateFormat day = new SimpleDateFormat("d", Locale.getDefault());

        String period = month.format(startDate) + " " + day.format(startDate);

        Calendar s = Calendar.getInstance();
        s.setTime(startDate);

        Calendar e = Calendar.getInstance();
        e.setTime(endDate);

        if (s.get(Calendar.DAY_OF_MONTH) != e.get(Calendar.DAY_OF_MONTH)) {
            period += " - " + day.format(endDate);
        }

        return period;
    }

    @Override
    public int getItemType() {
        return ViewType.DAY;
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
    public boolean equals(CalendarBase event) {
        if (event instanceof Day) {
            Day d = (Day) event;
            return d.startDate.equals(startDate) && d.endDate.equals(endDate);
        }
        return false;
    }

    public Date getEndDate() {
        return endDate;
    }

}
