package com.tinf.qmobile.holder.calendar.horizontal;

import android.content.Context;
import android.view.View;

import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.CalendarEventSimpleHBinding;
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
  public void bind(EventSimple event, Context context) {
    binding.title.setText(event.getTitle());
    binding.date.setText(event.getStartDateString());

    if (event.isRanged())
      binding.title.append(" " + String.format(Locale.getDefault(),
                                               context.getString(R.string.event_until),
                                               event.getEndDateString()));
  }

}
