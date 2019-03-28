package com.tinf.qmobile.ViewHolder.Calendar;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tinf.qmobile.Activity.Calendar.CreateEventActivity;
import com.tinf.qmobile.Class.Calendario.Base.EventBase;
import com.tinf.qmobile.Class.Calendario.EventUser;
import com.tinf.qmobile.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EventDefaultViewHolder extends CalendarioViewHolder<EventBase> {
    @BindView(R.id.calendar_default_title)       public TextView title;
    @BindView(R.id.caledar_default_description)  public TextView description;
    @BindView(R.id.calendar_default_card)        public LinearLayout card;
    @BindView(R.id.calendar_default_header)      public FrameLayout header;

    public EventDefaultViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(EventBase event, Context context) {
        title.setText(event.getTitle());
        description.setText(event.getDescription());
        card.setBackgroundColor(event.getColor());
        CalendarioViewHolder.setHeader(header, event, context);

        if (event instanceof EventUser) {
            card.setOnClickListener(v -> {
                Intent intent = new Intent(context, CreateEventActivity.class);
                intent.putExtra("ID", event.id);
                context.startActivity(intent);
            });
        }
    }

}
