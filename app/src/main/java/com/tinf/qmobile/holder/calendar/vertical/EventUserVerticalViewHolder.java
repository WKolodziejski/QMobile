package com.tinf.qmobile.holder.calendar.vertical;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.databinding.CalendarEventUserVBinding;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.utility.User;
import java.util.Locale;
import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.model.ViewType.EVENT;
import static com.tinf.qmobile.model.ViewType.USER;

public class EventUserVerticalViewHolder extends CalendarViewHolder<EventUser> {
    private final CalendarEventUserVBinding binding;

    private final static Drawable picture = User.getProfilePicture(getContext());

    public EventUserVerticalViewHolder(View view) {
        super(view);
        binding = CalendarEventUserVBinding.bind(view);
    }

    @Override
    public void bind(EventUser event, Context context) {
        binding.title.setText(event.getTitle().isEmpty() ? context.getString(R.string.event_no_title) : event.getTitle());

        if (event.isRanged())
            binding.title.append(" " + String.format(Locale.getDefault(), context.getString(R.string.event_until), event.getEndDateString()));

        if (event.getDescription().isEmpty()) {
            binding.description.setVisibility(View.GONE);
        } else {
            binding.description.setText(event.getDescription());
            binding.description.setVisibility(View.VISIBLE);
        }

        binding.card.setBackgroundColor(event.getColor());

        if (picture != null)
            binding.image.setImageDrawable(picture);

        binding.card.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("TYPE", EVENT);
            intent.putExtra("ID", event.id);
            context.startActivity(intent);
        });

        if (event.isHeader) {
            binding.header.day.setText(event.getWeekString());
            binding.header.number.setText(event.getDayString());
            binding.header.layout.setVisibility(View.VISIBLE);
        } else {
            binding.header.layout.setVisibility(View.INVISIBLE);
        }
    }

}
