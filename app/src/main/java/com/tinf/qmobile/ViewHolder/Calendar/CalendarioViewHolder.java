package com.tinf.qmobile.ViewHolder.Calendar;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tinf.qmobile.Class.Calendario.Base.CalendarBase;
import com.tinf.qmobile.Class.Calendario.Base.EventBase;
import com.tinf.qmobile.R;

import butterknife.ButterKnife;

public abstract class CalendarioViewHolder<T extends CalendarBase> extends RecyclerView.ViewHolder {

    public CalendarioViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public abstract void bind(T calendar, Context context);

    public static void setHeader(FrameLayout header, EventBase event, Context context) {
        final boolean isRanged = event.isRanged();

        final int layout = isRanged ? R.layout.header_range_day : R.layout.header_single_day;

        final View view = LayoutInflater.from(context).inflate(layout, header, false);
        if (header.getChildCount() > 0 ) {
            header.removeAllViews();
        }

        header.addView(view, 0);

        if (isRanged) {
            TextView start = (TextView) view.findViewById(R.id.calendar_header_day_start);
            TextView end = (TextView) view.findViewById(R.id.calendar_header_day_end);

            start.setText(event.getStartDay());
            end.setText(event.getEndDay());

        } else {
            TextView day = (TextView) view.findViewById(R.id.calendar_header_simple_day);

            day.setText(event.getStartDay());
        }
    }

}