package com.tinf.qmobile.holder.calendar;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.model.calendar.CalendarBase;

public abstract class CalendarViewHolder<T extends CalendarBase> extends RecyclerView.ViewHolder {

  public CalendarViewHolder(View view) {
    super(view);
  }

  public abstract void bind(T calendar, Context context);

}