package com.tinf.qmobile.Class.Calendario;

import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.Base.EventBase;
import com.tinf.qmobile.Class.Materias.Matter;
import com.tinf.qmobile.R;

import io.objectbox.annotation.Entity;
import io.objectbox.relation.ToOne;

@Entity
public class EventUser extends EventBase {
    //@Id public long id;
    public ToOne<Matter> matter;
    private long alarm;

    public EventUser(String title, long startTime, long alarm) {
        super(title, startTime);
        this.alarm = alarm;
    }

    public EventUser(String title, long startTime, long endTime, long alarm) {
        super(title, startTime, endTime);
        this.alarm = alarm;
    }

    @Override
    public int getColor() {
        if (super.getColor() != App.getContext().getResources().getColor(R.color.colorAccent)) {
            return super.getColor();

        } else if (matter.getTargetId() != 0) {
            return matter.getTarget().getColor();

        } else return super.getColor();
    }

    /*
     * Required methods
     */

    public EventUser() {
        super();
    }

    public long getAlarm() {
        return alarm;
    }

    @Override
    public int getItemType() {
        return ViewType.DEFAULT;
    }

}
