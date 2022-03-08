package com.tinf.qmobile.holder.calendar.vertical;

import android.content.Context;
import android.view.View;

import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.CalendarEventSimpleVBinding;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.calendar.EventSimple;

import java.util.Locale;

public class EventSimpleVerticalViewHolder extends CalendarViewHolder<EventSimple> {
    private final CalendarEventSimpleVBinding binding;

    public EventSimpleVerticalViewHolder(View view) {
        super(view);
        binding = CalendarEventSimpleVBinding.bind(view);
    }

    @Override
    public void bind(EventSimple event, Context context) {
        binding.title.setText(event.getTitle());

        if (event.isRanged())
            binding.title.append(" " + String.format(Locale.getDefault(), context.getString(R.string.event_until), event.getEndDateString()));

        if (event.isHeader) {
            binding.header.day.setText(event.getWeekString());
            binding.header.number.setText(event.getDayString());
            binding.header.layout.setVisibility(View.VISIBLE);
        } else {
            binding.header.layout.setVisibility(View.INVISIBLE);
        }
    }

}
