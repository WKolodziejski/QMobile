package com.tinf.qmobile.Class.Calendario;

import com.tinf.qmobile.Class.Calendario.Base.CalendarBase;

import java.text.SimpleDateFormat;
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

        if (startDate.getDay() != endDate.getDay()) {
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

}
