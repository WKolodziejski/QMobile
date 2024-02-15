package com.tinf.qmobile.holder.calendar;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.ColorRoles;
import com.tinf.qmobile.databinding.CalendarHeaderDaySingleBinding;
import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.utility.ColorsUtils;
import com.tinf.qmobile.utility.DesignUtils;

public abstract class CalendarViewHolder<T extends CalendarBase> extends RecyclerView.ViewHolder {

  public CalendarViewHolder(View view) {
    super(view);
  }

  protected abstract CalendarHeaderDaySingleBinding bind(T calendar,
                                                         Context context);

  public void bind(T calendar,
                   Context context,
                   int position) {
    CalendarHeaderDaySingleBinding header = bind(calendar, context);

    if (header == null)
      return;

    if (calendar.isHeader()) {
      header.day.setText(calendar.getWeekString());
      header.number.setText(calendar.getDayString());
      header.layout.setVisibility(View.VISIBLE);
    } else {
      header.layout.setVisibility(View.INVISIBLE);
    }

    if (calendar.isToday()) {
      ColorRoles colorRoles = ColorsUtils.getColorRoles(context,
                                                        com.google.android.material.R.attr.colorPrimaryContainer);

      header.number.setBackgroundTintList(ColorStateList.valueOf(colorRoles.getAccentContainer()));
      header.number.setTextColor(colorRoles.getOnAccentContainer());
    } else {
      header.number.setBackgroundTintList(ColorStateList.valueOf(
          ColorsUtils.getColor(context, com.google.android.material.R.attr.colorSurface)));
      header.number.setTextColor(
          ColorsUtils.getColor(context, com.google.android.material.R.attr.colorOnSurface));
    }

    // TODO: ajustar margens dinamicamente
//    ViewGroup.LayoutParams params = binding.header.layout.getLayoutParams();
//    params.height = !clazz.isHeader ? 0 : ViewGroup.LayoutParams.WRAP_CONTENT;
//    binding.header.layout.setLayoutParams(params);
  }

}