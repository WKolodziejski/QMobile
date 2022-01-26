package com.tinf.qmobile.holder.calendar.horizontal;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.databinding.CalendarEventUserHBinding;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.calendar.EventUser;
import java.util.Locale;

import static com.tinf.qmobile.model.ViewType.EVENT;
import static com.tinf.qmobile.model.ViewType.USER;

public class EventUserHorizontalViewHolder extends CalendarViewHolder<EventUser> {
    private final CalendarEventUserHBinding binding;

    public EventUserHorizontalViewHolder(View view) {
        super(view);
        binding = CalendarEventUserHBinding.bind(view);
    }

    @Override
    public void bind(EventUser event, Context context) {
        binding.title.setText(event.getTitle().isEmpty() ? context.getString(R.string.event_no_title) : event.getTitle());
        binding.date.setText(event.getStartDateString());

        if (event.isRanged())
            binding.title.append(" " + String.format(Locale.getDefault(), context.getString(R.string.event_until), event.getEndDateString()));

        if (event.getDescription().isEmpty()) {
            binding.description.setVisibility(View.GONE);
        } else {
            binding.description.setText(event.getDescription());
            binding.description.setVisibility(View.VISIBLE);
        }
        if (event.getMatter().isEmpty()) {
            binding.matter.setVisibility(View.GONE);
        } else {
            binding.matter.setVisibility(View.VISIBLE);
            binding.matter.setText(event.getMatter());
        }

        binding.card.setBackgroundColor(event.getColor());

        binding.card.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("TYPE", EVENT);
            intent.putExtra("ID", event.id);
            context.startActivity(intent);
        });
    }

}
