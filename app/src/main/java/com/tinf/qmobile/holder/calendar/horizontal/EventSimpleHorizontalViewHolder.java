package com.tinf.qmobile.holder.calendar.horizontal;

import static com.tinf.qmobile.model.ViewType.EVENT;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.databinding.CalendarEventSimpleHBinding;
import com.tinf.qmobile.databinding.CalendarHeaderDaySingleBinding;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.calendar.EventSimple;

import java.util.Locale;

public class EventSimpleHorizontalViewHolder extends CalendarViewHolder<EventSimple> {
  private final CalendarEventSimpleHBinding binding;

  public EventSimpleHorizontalViewHolder(View view) {
    super(view);
    binding = CalendarEventSimpleHBinding.bind(view);
  }

  @Override
  public CalendarHeaderDaySingleBinding bind(EventSimple event,
                                             Context context) {
    binding.title.setText(event.getTitle());
    binding.date.setText(event.getStartDateString());

    if (event.isRanged()) {
      binding.title.append(" " + String.format(Locale.getDefault(),
                                               context.getString(R.string.event_until),
                                               event.getEndDateString()));
    }

    binding.card.setOnClickListener(v -> {
      Intent intent = new Intent(context, EventViewActivity.class);
      intent.putExtra("TYPE", EVENT);
      intent.putExtra("ID", event.id);
      context.startActivity(intent);
    });

    return null;
  }

}
