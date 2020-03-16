package com.tinf.qmobile.holder.calendar.vertical;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.calendar.Month;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MonthViewHolder extends CalendarViewHolder<Month> {
    @BindView(R.id.calendario_month_title) public TextView title;

    public MonthViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Month month, Context context) {
        title.setText(month.getMonth());
    }

}
