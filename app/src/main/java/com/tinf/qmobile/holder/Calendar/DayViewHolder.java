package com.tinf.qmobile.holder.Calendar;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.tinf.qmobile.model.calendario.Day;
import com.tinf.qmobile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DayViewHolder extends CalendarioViewHolder<Day> {
    @BindView(R.id.calendario_day_title) public TextView title;

    public DayViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Day day, Context context) {
        title.setText(day.getDayPeriod());
    }
}
