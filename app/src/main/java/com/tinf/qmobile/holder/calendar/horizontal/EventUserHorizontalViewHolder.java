package com.tinf.qmobile.holder.calendar.horizontal;

import static com.tinf.qmobile.model.ViewType.EVENT;
import static com.tinf.qmobile.model.ViewType.USER;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.android.material.color.ColorRoles;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.databinding.CalendarEventUserHBinding;
import com.tinf.qmobile.databinding.CalendarHeaderDaySingleBinding;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.utility.ColorsUtils;

import java.util.Locale;

public class EventUserHorizontalViewHolder extends CalendarViewHolder<EventUser> {
  private final CalendarEventUserHBinding binding;

  public EventUserHorizontalViewHolder(View view) {
    super(view);
    binding = CalendarEventUserHBinding.bind(view);
  }

  @Override
  public CalendarHeaderDaySingleBinding bind(EventUser event,
                                             Context context) {
    ColorRoles colorRoles = ColorsUtils.harmonizeWithPrimary(context, event.getColor());


    binding.title.setText(
        event.getTitle()
             .isEmpty() ? context.getString(R.string.event_no_title) : event.getTitle());
    binding.date.setText(event.getStartDateString());

    if (event.isRanged())
      binding.title.append(" " + String.format(Locale.getDefault(),
                                               context.getString(R.string.event_until),
                                               event.getEndDateString()));

    if (event.getDescription()
             .isEmpty()) {
      binding.description.setVisibility(View.GONE);
    } else {
      binding.description.setText(event.getDescription());
      binding.description.setVisibility(View.VISIBLE);
    }
//    if (event.getMatter()
//             .isEmpty()) {
//      binding.matter.setVisibility(View.GONE);
//    } else {
//      binding.matter.setVisibility(View.VISIBLE);
//      binding.matter.setText(event.getMatter());
//    }

    binding.card.setBackgroundColor(colorRoles.getAccentContainer());
    binding.title.setTextColor(colorRoles.getOnAccentContainer());
    binding.description.setTextColor(colorRoles.getOnAccentContainer());
    binding.date.setTextColor(colorRoles.getOnAccentContainer());

    binding.card.setOnClickListener(v -> {
      Intent intent = new Intent(context, EventViewActivity.class);
      intent.putExtra("TYPE", USER);
      intent.putExtra("ID", event.id);
      intent.putExtra("LOOKUP", true);
      context.startActivity(intent);
    });

    return null;
  }

}
