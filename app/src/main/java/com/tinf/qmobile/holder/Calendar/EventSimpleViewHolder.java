package com.tinf.qmobile.holder.Calendar;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinf.qmobile.model.calendario.EventSimple;
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
