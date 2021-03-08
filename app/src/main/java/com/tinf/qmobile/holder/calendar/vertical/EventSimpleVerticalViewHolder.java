package com.tinf.qmobile.holder.calendar.vertical;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.calendar.EventSimple;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventSimpleVerticalViewHolder extends CalendarViewHolder<EventSimple> {
    @BindView(R.id.calendar_simple_title)       public TextView title;

    @BindView(R.id.calendar_header_simple_day_week)     TextView day;
    @BindView(R.id.calendar_header_simple_day_number)   TextView number;
    @BindView(R.id.calendar_header_simple_layout)       LinearLayout layout;

    public EventSimpleVerticalViewHolder(View view) {
        super(view);
    }

    @Override
    public void bind(EventSimple event, Context context) {
        title.setText(event.getTitle());

        if (event.isRanged())
            title.append(" " + String.format(Locale.getDefault(), context.getString(R.string.event_until), event.getEndDateString()));

        if (event.isHeader) {
            day.setText(event.getWeekString());
            number.setText(event.getDayString());
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.INVISIBLE);
        }
    }

}
