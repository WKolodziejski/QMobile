package com.tinf.qmobile.model.calendario;

import com.tinf.qmobile.model.calendario.Base.CalendarBase;

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

    public Date getEndDate() {
        return endDate;
    }

}
