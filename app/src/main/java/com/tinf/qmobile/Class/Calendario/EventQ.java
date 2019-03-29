package com.tinf.qmobile.Class.Calendario;

import com.tinf.qmobile.Class.Calendario.Base.EventBase;
import com.tinf.qmobile.Class.Materias.Matter;
import io.objectbox.annotation.Entity;
import io.objectbox.relation.ToOne;

@Entity
public class EventQ extends EventBase {
    public ToOne<Matter> matter;

    public EventQ(String title, long startTime) {
        super(title, startTime);
    }

    public EventQ(String title, long startTime, long endTime) {
        super(title, startTime, endTime);
    }

    @Override
    public int getColor() {
        if (matter.getTargetId() != 0) {
            return matter.getTarget().getColor();

        } else return super.getColor();
    }

    public String getMatter() {
        return matter.getTargetId() != 0 ? matter.getTarget().getTitle() : "";
    }

    /*
     * Required methods
     */

    public EventQ() {
        super();
    }

    @Override
    public int getItemType() {
        return ViewType.DEFAULT;
    }

}
