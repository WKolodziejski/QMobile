package com.tinf.qmobile.model.calendar;

import org.joda.time.LocalDate;
import java.util.Date;
import java.util.List;

import static com.tinf.qmobile.model.ViewType.MONTH;

public class Month2 implements CalendarBase {
    private LocalDate date;
    private List<Day2> days;

    public Month2(LocalDate date) {
        this.date = date;
    }

    @Override
    public int getItemType() {
        return MONTH;
    }

    @Override
    public Date getDate() {
        return date.toDate();
    }

    @Override
    public int getDay() {
        return date.dayOfMonth().getMaximumValue();
    }

    @Override
    public int getYear() {
        return date.getYear();
    }

    public int getMonth() {
        return date.getMonthOfYear();
    }

    public String getName() {
        return date.toString("MMMM");
    }

    public int getNoDay() {
       return date.dayOfMonth().withMinimumValue().dayOfWeek().get() % 7;
    }

    @Override
    public boolean equals(CalendarBase event) {
        return false;
    }

    public void setDays(List<Day2> days) {
        this.days = days;
    }

    public List<Day2> getDays() {
        return days;
    }

}
