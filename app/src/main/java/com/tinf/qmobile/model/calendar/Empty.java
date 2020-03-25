package com.tinf.qmobile.model.calendar;

import static com.tinf.qmobile.model.calendar.CalendarBase.ViewType.EMPTY;

public class Empty extends EventBase {

    @Override
    public int getItemType() {
        return EMPTY;
    }

}
