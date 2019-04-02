package com.tinf.qmobile.ViewHolder.Calendar.TEST;

import com.chad.library.adapter.base.BaseSectionMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tinf.qmobile.Class.Calendario.Base.CalendarBase;
import com.tinf.qmobile.R;
import java.util.List;

public class MultipleItemQuickAdapter extends BaseSectionMultiItemQuickAdapter<DaySection, BaseViewHolder> {

    public MultipleItemQuickAdapter(int sectionHeadResId, List data) {
        super(sectionHeadResId, data);
        addItemType(CalendarBase.ViewType.IMAGE, R.layout.list_event_image);
        addItemType(CalendarBase.ViewType.SIMPLE, R.layout.list_event_simple);
        addItemType(CalendarBase.ViewType.USER, R.layout.list_event_user);
        addItemType(CalendarBase.ViewType.JOURNAL, R.layout.list_event_journal);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, DaySection item) {
        helper.setText(R.id.calendario_day_title, item.header);
    }

    @Override
    protected void convert(BaseViewHolder helper, DaySection item) {
        switch (item.getItemType()) {
            case CalendarBase.ViewType.JOURNAL:
                helper.setText(R.id.calendar_default_title, item.t.getTitle());
        }
    }

}
