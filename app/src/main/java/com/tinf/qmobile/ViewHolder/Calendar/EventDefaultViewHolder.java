package com.tinf.qmobile.ViewHolder.Calendar;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tinf.qmobile.Activity.EventViewActivity;
import com.tinf.qmobile.Class.Calendario.Base.CalendarBase;
import com.tinf.qmobile.Class.Calendario.Base.EventBase;
import com.tinf.qmobile.Class.Calendario.EventJournal;
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
    public void bind(EventBase event, Context context, boolean enableOnClick) {
        title.setText(event.getTitle().isEmpty() ? context.getString(R.string.event_no_title) : event.getTitle());
        description.setText(event.getDescription());
        card.setBackgroundColor(event.getColor());
        CalendarioViewHolder.setHeader(header, event, context);

        if (enableOnClick) {
            card.setOnClickListener(v -> {
                Intent intent = new Intent(context, EventViewActivity.class);

                int type = event instanceof EventUser ? CalendarBase.ViewType.USER : CalendarBase.ViewType.JOURNAL;

                intent.putExtra("TYPE", type);

                long id;

                if (type == CalendarBase.ViewType.JOURNAL) {
                    id = ((EventJournal) event).getJournal().id;
                } else {
                    id = event.id;
                }

                intent.putExtra("ID", id);

                context.startActivity(intent);
            });
        }
    }

}
