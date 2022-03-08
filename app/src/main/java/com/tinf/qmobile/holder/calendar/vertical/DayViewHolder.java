package com.tinf.qmobile.holder.calendar.vertical;

import android.content.Context;
import android.view.View;

import com.tinf.qmobile.databinding.CalendarHeaderDayRangeBinding;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.calendar.Day;

public class DayViewHolder extends CalendarViewHolder<Day> {
    private final CalendarHeaderDayRangeBinding binding;

    public DayViewHolder(View view) {
        super(view);
        binding = CalendarHeaderDayRangeBinding.bind(view);
    }

    @Override
    public void bind(Day day, Context context) {
        binding.title.setText(day.getDayPeriod());
    }

}
