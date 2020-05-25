package com.tinf.qmobile.model.calendar;

import java.util.Date;

public interface CalendarBase<T extends CalendarBase> {

    int getItemType();
    Date getDate();
    int getDay();
    int getYear();

    boolean equals(T event);

}
