package com.tinf.qmobile.holder.calendar;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.model.calendar.Header;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HeaderViewHolder extends CalendarViewHolder<Header> {
    @BindView(R.id.calendar_header_simple_day)      TextView day;

    public HeaderViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Header calendar, Context context) {
        day.setText(calendar.getDayString());
    }

}
