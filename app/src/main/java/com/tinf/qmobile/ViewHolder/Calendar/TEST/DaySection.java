package com.tinf.qmobile.ViewHolder.Calendar.TEST;

import com.chad.library.adapter.base.entity.SectionMultiEntity;
import com.tinf.qmobile.Class.Calendario.Base.CalendarBase;
import com.tinf.qmobile.Class.Calendario.Day;

public class DaySection extends SectionMultiEntity<EventBaseTEST> {
    private boolean isMore;
    @CalendarBase.ViewType private int type;

    public DaySection(Day day) {
        super(true, day.getDayPeriod());
        type = CalendarBase.ViewType.DAY;
    }

    public DaySection(EventBaseTEST t) {
        super(t);
        type = t.getItemType();
    }

    @Override
    public int getItemType() {
        return type;
    }
    
}
