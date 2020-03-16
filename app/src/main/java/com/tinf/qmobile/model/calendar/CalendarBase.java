package com.tinf.qmobile.model.calendar;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import static com.tinf.qmobile.model.calendar.CalendarBase.ViewType.DAY;
import static com.tinf.qmobile.model.calendar.CalendarBase.ViewType.JOURNAL;
import static com.tinf.qmobile.model.calendar.CalendarBase.ViewType.MONTH;
import static com.tinf.qmobile.model.calendar.CalendarBase.ViewType.Q;
import static com.tinf.qmobile.model.calendar.CalendarBase.ViewType.SIMPLE;
import static com.tinf.qmobile.model.calendar.CalendarBase.ViewType.USER;

public interface CalendarBase<T extends CalendarBase> {

    @IntDef({SIMPLE, MONTH, USER, JOURNAL, Q, DAY})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
        //int DEFAULT = 100;
        int SIMPLE = 300;
        int MONTH = 400;
        int USER = 500;
        int JOURNAL = 200;
        int Q = 700;
        int DAY = 800;
        int HEADER = 900;
    }

    int getItemType();
    Date getDate();
    int getDay();
    int getYear();

    boolean equals(T event);

}
