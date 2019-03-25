package com.tinf.qmobile.ViewHolder.Calendar;

import android.view.View;
import android.widget.TextView;
import com.tinf.qmobile.Class.Calendario.Month;
import com.tinf.qmobile.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MonthViewHolder extends CalendarioViewHolder<Month> {
    @BindView(R.id.calendario_month_title) public TextView title;

    public MonthViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Month month) {
        title.setText(month.getMonth());
    }
}
