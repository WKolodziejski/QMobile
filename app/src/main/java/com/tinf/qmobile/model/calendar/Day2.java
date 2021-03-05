package com.tinf.qmobile.model.calendar;

import org.joda.time.DateTime;
import java.util.Date;
import static com.tinf.qmobile.model.ViewType.DAY;

public class Day2 implements CalendarBase {
    private DateTime date;
    private boolean isList;
    private boolean isToday;

    public Day2(DateTime date) {
        this.date = date;
    }

    @Override
    public int getItemType() {
        return DAY;
    }

    @Override
    public Date getDate() {
        return date.toDate();
    }

    @Override
    public int getDay() {
        return date.getDayOfMonth();
    }

    @Override
    public int getYear() {
        return date.getYear();
    }

    public int getMonth() {
        return date.getMonthOfYear();
    }

    @Override
    public boolean equals(CalendarBase event) {
        return false;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }

    public void setToday(boolean today) {
        isToday = today;
    }

}
