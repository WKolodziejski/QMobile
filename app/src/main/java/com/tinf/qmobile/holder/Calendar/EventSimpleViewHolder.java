package com.tinf.qmobile.holder.Calendar;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.model.calendar.EventSimple;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventSimpleViewHolder extends CalendarViewHolder<EventSimple> {
    @BindView(R.id.calendar_simple_title)       public TextView title;
    @BindView(R.id.calendar_simple_header)      public FrameLayout header;

    public EventSimpleViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(EventSimple event, Context context) {
        title.setText(event.getTitle());
        CalendarViewHolder.setHeader(header, event, context);
    }

}
