package com.tinf.qmobile.ViewHolder.Calendar;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinf.qmobile.Class.Calendario.Event;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.Calendar.CalendarioViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventDefaultViewHolder extends CalendarioViewHolder<Event> {
    @BindView(R.id.calendario_default_dia)         public TextView dia;
    @BindView(R.id.calendario_default_title)       public TextView title;
    @BindView(R.id.caledario_default_description)  public TextView description;
    @BindView(R.id.calendario_default_header)      public LinearLayout header;

    public EventDefaultViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Event event) {
        dia.setText(event.getDay());
        title.setText(event.getTitle());
        description.setText(event.getDescription());
        header.setBackgroundColor(event.getColor());
    }

}
