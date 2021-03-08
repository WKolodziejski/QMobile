package com.tinf.qmobile.model.calendar;

import org.joda.time.LocalDate;

import java.util.Date;

public interface CalendarBase<T extends CalendarBase> {

    int getItemType();
    Date getDate();
    int getDay();
    int getYear();
    LocalDate getHashKey();
    boolean isHeader();
    boolean equals(T event);

}
