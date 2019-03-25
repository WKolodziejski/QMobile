package com.tinf.qmobile.ViewHolder.Calendar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.tinf.qmobile.Class.Calendario.Event;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.Calendar.CalendarioViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventImageViewHolder extends CalendarioViewHolder<Event> {
    @BindView(R.id.calendario_image_image) public ImageView image;
    @BindView(R.id.calendario_image_start) public TextView start;
    @BindView(R.id.calendario_image_end)   public TextView end;
    @BindView(R.id.calendario_image_title) public TextView title;

    public EventImageViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Event event) {
        image.setImageResource(event.getImage());
        title.setText(event.getTitle());
        start.setText(event.getDay());
        end.setText(event.getEndDay());
    }
}
