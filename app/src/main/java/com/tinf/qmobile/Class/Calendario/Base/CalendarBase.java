package com.tinf.qmobile.Class.Calendario.Base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import androidx.annotation.IntDef;
import static com.tinf.qmobile.Class.Calendario.Base.CalendarBase.ImageType.CARNAVAL;
import static com.tinf.qmobile.Class.Calendario.Base.CalendarBase.ImageType.CHRISTMAS;
import static com.tinf.qmobile.Class.Calendario.Base.CalendarBase.ImageType.RECESS;
import static com.tinf.qmobile.Class.Calendario.Base.CalendarBase.ImageType.VACATION;
import static com.tinf.qmobile.Class.Calendario.Base.CalendarBase.ViewType.DAY;
import static com.tinf.qmobile.Class.Calendario.Base.CalendarBase.ViewType.DEFAULT;
import static com.tinf.qmobile.Class.Calendario.Base.CalendarBase.ViewType.IMAGE;
import static com.tinf.qmobile.Class.Calendario.Base.CalendarBase.ViewType.JOURNAL;
import static com.tinf.qmobile.Class.Calendario.Base.CalendarBase.ViewType.MONTH;
import static com.tinf.qmobile.Class.Calendario.Base.CalendarBase.ViewType.Q;
import static com.tinf.qmobile.Class.Calendario.Base.CalendarBase.ViewType.SIMPLE;
import static com.tinf.qmobile.Class.Calendario.Base.CalendarBase.ViewType.USER;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

public interface CalendarBase {

    @IntDef({DEFAULT, IMAGE, SIMPLE, MONTH, USER, JOURNAL, Q, DAY})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
        int DEFAULT = 100;
        int IMAGE = 200;
        int SIMPLE = 300;
        int MONTH = 400;
        int USER = 500;
        int JOURNAL = 600;
        int Q = 700;
        int DAY = 800;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CHRISTMAS, VACATION, CARNAVAL, RECESS})
    @interface ImageType {
        int CHRISTMAS = 1;
        int VACATION = 2;
        int CARNAVAL = 3;
        int RECESS = 4;
    }

    int getItemType();
    Date getDate();

}
