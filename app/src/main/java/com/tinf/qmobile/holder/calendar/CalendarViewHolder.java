package com.tinf.qmobile.holder.calendar;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.model.calendar.CalendarBase;

import butterknife.ButterKnife;

public abstract class CalendarViewHolder<T extends CalendarBase> extends RecyclerView.ViewHolder {

    public CalendarViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public abstract void bind(T calendar, Context context);

    /*public static void setHeader(FrameLayout header, EventBase event, Context context) {
        final boolean isRanged = event.isRanged();

        final int layout = isRanged ? R.layout.calendar_header_range_day : R.layout.calendar_header_day_single;

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
    }*/

}