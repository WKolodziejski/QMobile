package com.tinf.qmobile.model;

import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.model.calendar.EventBase;

import java.util.EnumMap;

import static com.tinf.qmobile.model.ViewType.EMPTY;

public class Empty extends EventBase implements Queryable {
    int type = EMPTY;

    public Empty() {}

    public Empty(int type) {
        this.type = type;
    }

    @Override
    public int getItemType() {
        return type;
    }

    @Override
    public long getId() {
        return EMPTY;
    }

    @Override
    public boolean isSame(Queryable queryable) {
        return queryable.equals(this);
    }

}
