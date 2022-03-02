package com.tinf.qmobile.model.calendar;

import com.tinf.qmobile.model.Queryable;

import org.joda.time.LocalDate;

import java.util.Date;

public interface CalendarBase<T extends CalendarBase> extends Queryable {

    Date getDate();
    int getDay();
    int getYear();
    LocalDate getHashKey();
    boolean isHeader();
    //boolean equals(T event);

}
