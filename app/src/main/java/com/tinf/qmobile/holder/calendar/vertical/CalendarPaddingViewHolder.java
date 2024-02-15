package com.tinf.qmobile.holder.calendar.vertical;

import android.content.Context;
import android.view.View;

import com.tinf.qmobile.databinding.CalendarHeaderDaySingleBinding;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.calendar.Padding;

public class CalendarPaddingViewHolder extends CalendarViewHolder<Padding> {

  public CalendarPaddingViewHolder(View view) {
    super(view);
  }

  @Override
  protected CalendarHeaderDaySingleBinding bind(Padding calendar,
                                                Context context) {
    return null;
  }
}
