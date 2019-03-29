package com.tinf.qmobile.ViewHolder.Calendar;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.EventImage;
import com.tinf.qmobile.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EventImageViewHolder extends CalendarioViewHolder<EventImage> {
    @BindView(R.id.calendar_image_image) public ImageView image;
    @BindView(R.id.calendar_image_title) public TextView title;
    @BindView(R.id.calendar_image_header) public FrameLayout header;

    public EventImageViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(EventImage event, Context context, boolean enableOnClick) {
        image.setImageResource(event.getImage());
        title.setText(event.getTitle());
        CalendarioViewHolder.setHeader(header, event, context);
    }
}
