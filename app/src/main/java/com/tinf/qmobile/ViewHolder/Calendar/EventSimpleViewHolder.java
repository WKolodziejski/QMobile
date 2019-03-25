package com.tinf.qmobile.ViewHolder.Calendar;

import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinf.qmobile.Class.Calendario.Event;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.Calendar.CalendarioViewHolder;

public class EventSimpleViewHolder extends CalendarioViewHolder<Event> {
    @BindView(R.id.calendario_simple_dia)         public TextView dia;
    @BindView(R.id.calendario_simple_title)       public TextView title;

    public EventSimpleViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Event event) {
        dia.setText(event.getDay());
        title.setText(event.getTitle());
    }

}
