package com.tinf.qmobile.holder.calendar.vertical;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.utility.User;

import java.util.Locale;

import butterknife.BindView;

import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.model.ViewType.USER;

public class EventUserVerticalViewHolder extends CalendarViewHolder<EventUser> {
    @BindView(R.id.calendar_user_title)         TextView title;
    @BindView(R.id.calendar_user_description)   TextView description;
    @BindView(R.id.calendar_user_card)          ConstraintLayout card;
    @BindView(R.id.calendar_user_img)           ImageView image;

    @BindView(R.id.calendar_header_simple_day_week)     TextView day;
    @BindView(R.id.calendar_header_simple_day_number)   TextView number;
    @BindView(R.id.calendar_header_simple_layout)       LinearLayout layout;

    private final static Drawable picture = User.getProfilePicture(getContext());

    public EventUserVerticalViewHolder(View view) {
        super(view);
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

        card.setBackgroundColor(event.getColor());

        if (picture != null)
            image.setImageDrawable(picture);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("TYPE", USER);
            intent.putExtra("ID", event.id);
            context.startActivity(intent);
        });

        if (event.isHeader) {
            day.setText(event.getWeekString());
            number.setText(event.getDayString());
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.INVISIBLE);
        }
    }

}
