package com.tinf.qmobile.holder.calendar.horizontal;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.calendar.EventSimple;

import java.util.Locale;

import butterknife.BindView;

public class EventSimpleHorizontalViewHolder extends CalendarViewHolder<EventSimple> {
    @BindView(R.id.calendar_simple_title)       public TextView title;
    @BindView(R.id.calendar_simple_date)        public TextView date;

    public EventSimpleHorizontalViewHolder(View view) {
        super(view);
    }

    @Override
    public void bind(EventSimple event, Context context) {
        title.setText(event.getTitle());
        date.setText(event.getStartDateString());

        if (event.isRanged())
            title.append(" " + String.format(Locale.getDefault(), context.getString(R.string.event_until), event.getEndDateString()));
    }

}
