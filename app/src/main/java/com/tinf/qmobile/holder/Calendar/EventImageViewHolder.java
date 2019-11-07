package com.tinf.qmobile.holder.Calendar;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.model.calendar.EventImage;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventImageViewHolder extends CalendarViewHolder<EventImage> {
    @BindView(R.id.calendar_image_image) public ImageView image;
    @BindView(R.id.calendar_image_title) public TextView title;
    @BindView(R.id.calendar_image_header) public FrameLayout header;

    public EventImageViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(EventImage event, Context context) {
        image.setImageResource(event.getImage());
        title.setText(event.getTitle());
        CalendarViewHolder.setHeader(header, event, context);
    }
}
