package com.tinf.qmobile.holder.calendar;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.calendar.base.CalendarBase;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventUserViewHolder extends CalendarViewHolder<EventUser> {
    @BindView(R.id.calendar_user_title)       public TextView title;
    @BindView(R.id.calendar_user_description) public TextView description;
    @BindView(R.id.calendar_user_matter)      public TextView matter;
    @BindView(R.id.calendar_user_card)        public LinearLayout card;

    public EventUserViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(EventUser event, Context context) {
        title.setText(event.getTitle().isEmpty() ? context.getString(R.string.event_no_title) : event.getTitle());

        if (event.isRanged())
            title.append(" " + String.format(Locale.getDefault(), context.getString(R.string.event_until), event.getEndDateString()));

        if (event.getDescription().isEmpty()) {
            description.setVisibility(View.GONE);
        } else {
            description.setText(event.getDescription());
            description.setVisibility(View.VISIBLE);
        }
        if (event.getMatter().isEmpty()) {
            matter.setVisibility(View.GONE);
        } else {
            matter.setVisibility(View.VISIBLE);
            matter.setText(event.getMatter());
        }
        card.setBackgroundColor(event.getColor());

        card.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("TYPE", CalendarBase.ViewType.USER);
            intent.putExtra("ID", event.id);
            context.startActivity(intent);
        });
    }

}
