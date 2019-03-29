package com.tinf.qmobile.ViewHolder.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinf.qmobile.Class.Calendario.EventSimple;
import com.tinf.qmobile.R;

public class EventSimpleViewHolder extends CalendarioViewHolder<EventSimple> {
    @BindView(R.id.calendar_simple_title)       public TextView title;
    @BindView(R.id.calendar_simple_header)        public FrameLayout header;

    public EventSimpleViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(EventSimple event, Context context, boolean enableOnClick) {
        title.setText(event.getTitle());
        CalendarioViewHolder.setHeader(header, event, context);
    }

}
