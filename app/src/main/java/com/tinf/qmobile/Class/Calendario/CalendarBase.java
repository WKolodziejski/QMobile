package com.tinf.qmobile.Class.Calendario;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import androidx.annotation.IntDef;

import static com.tinf.qmobile.Class.Calendario.CalendarBase.ViewType.DAY;
import static com.tinf.qmobile.Class.Calendario.CalendarBase.ViewType.DEFAULT;
import static com.tinf.qmobile.Class.Calendario.CalendarBase.ViewType.IMAGE;
import static com.tinf.qmobile.Class.Calendario.CalendarBase.ViewType.MONTH;
import static com.tinf.qmobile.Class.Calendario.CalendarBase.ViewType.SIMPLE;

public interface CalendarBase {

    @IntDef({DEFAULT, IMAGE, SIMPLE, MONTH, DAY})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
        int DEFAULT = 100;
        int IMAGE = 200;
        int SIMPLE = 300;
        int MONTH = 1;
        int DAY = 1;
    }

    int getItemType();
}
