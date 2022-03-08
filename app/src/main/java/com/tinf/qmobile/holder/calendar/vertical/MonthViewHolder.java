package com.tinf.qmobile.holder.calendar.vertical;

import android.content.Context;
import android.view.View;

import com.tinf.qmobile.databinding.CalendarHeaderMonthBinding;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.calendar.Month;

public class MonthViewHolder extends CalendarViewHolder<Month> {
    private final CalendarHeaderMonthBinding binding;

    public MonthViewHolder(View view) {
        super(view);
        binding = CalendarHeaderMonthBinding.bind(view);
    }

    @Override
    public void bind(Month month, Context context) {
        binding.month.setText(month.getMonth());
    }

}
