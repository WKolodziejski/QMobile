package com.tinf.qmobile.ViewHolder.Calendar;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
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
    public void bind(Month month, Context context, boolean enableOnClick) {
        title.setText(month.getMonth());
    }

}
