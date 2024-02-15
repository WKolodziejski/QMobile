package com.tinf.qmobile.holder.calendar.vertical;

import static com.tinf.qmobile.model.ViewType.CLASS;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;

import com.google.android.material.color.ColorRoles;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.databinding.CalendarEventClazzVBinding;
import com.tinf.qmobile.databinding.CalendarHeaderDaySingleBinding;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.matter.Clazz;
import com.tinf.qmobile.utility.ColorsUtils;

public class EventClazzVerticalViewHolder extends CalendarViewHolder<Clazz> {
  private final CalendarEventClazzVBinding binding;

  public EventClazzVerticalViewHolder(View view) {
    super(view);
    binding = CalendarEventClazzVBinding.bind(view);
  }

  @Override
  protected CalendarHeaderDaySingleBinding bind(Clazz clazz,
                                                Context context) {
    ColorRoles colorRoles = ColorsUtils.harmonizeWithPrimary(context, clazz.getColor());

    binding.title.setText(clazz.getTitle());
    binding.matter.setText(clazz.getMatter());
    binding.card.setStrokeColor(colorRoles.getAccentContainer());
    binding.title.setTextColor(colorRoles.getAccent());
    binding.matter.setTextColor(colorRoles.getAccent());
    binding.absence.setVisibility(clazz.getAbsences_() > 0 ? View.VISIBLE : View.GONE);
    binding.absence.setImageTintList(ColorStateList.valueOf(colorRoles.getAccent()));

    binding.card.setOnClickListener(v -> {
      Intent intent = new Intent(context, EventViewActivity.class);
      intent.putExtra("TYPE", CLASS);
      intent.putExtra("ID", clazz.id);
      intent.putExtra("LOOKUP", true);
      context.startActivity(intent);
    });

    return binding.header;
  }

}
