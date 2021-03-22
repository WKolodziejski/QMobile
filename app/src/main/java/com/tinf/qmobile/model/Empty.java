package com.tinf.qmobile.model;

import com.tinf.qmobile.model.calendar.EventBase;

import static com.tinf.qmobile.model.ViewType.EMPTY;

public class Empty extends EventBase implements Queryable {

    @Override
    public int getItemType() {
        return EMPTY;
    }

}
