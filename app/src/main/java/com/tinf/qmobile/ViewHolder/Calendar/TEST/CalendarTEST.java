package com.tinf.qmobile.ViewHolder.Calendar.TEST;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.entity.SectionMultiEntity;
import com.tinf.qmobile.Class.Calendario.CalendarBase;

public class CalendarTEST extends SectionMultiEntity<MultiItemEntity> {
    private int viewType;

    public CalendarTEST(boolean isHeader, String header) {
        super(isHeader, header);
        viewType = CalendarBase.ViewType.DAY;
    }

    public CalendarTEST(MultiItemEntity item) {
        super(item);
        viewType = item.getItemType();
    }

    @Override
    public int getItemType() {
        return viewType;
    }
}
