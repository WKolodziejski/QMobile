package com.tinf.qmobile.holder.Calendar;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.tinf.qmobile.model.calendario.Month;
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
