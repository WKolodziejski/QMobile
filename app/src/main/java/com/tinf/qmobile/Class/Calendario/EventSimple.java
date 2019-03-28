package com.tinf.qmobile.Class.Calendario;

import com.tinf.qmobile.Class.Calendario.Base.EventBase;
import io.objectbox.annotation.Entity;

@Entity
public class EventSimple extends EventBase {

    public EventSimple(String title, long startTime) {
        super(title, startTime);
    }

    public EventSimple(String title, long startTime, long endTime) {
        super(title, startTime, endTime);
    }

    /*
     * Required methods
     */

    public EventSimple() {
        super();
    }

    @Override
    public int getItemType() {
        return ViewType.SIMPLE;
    }

}
