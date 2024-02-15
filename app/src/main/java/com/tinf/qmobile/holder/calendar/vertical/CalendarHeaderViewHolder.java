package com.tinf.qmobile.holder.calendar.vertical;

import android.content.Context;
import android.view.View;

import com.tinf.qmobile.databinding.CalendarHeaderDaySingleBinding;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.calendar.Header;

public class CalendarHeaderViewHolder extends CalendarViewHolder<Header> {

  public CalendarHeaderViewHolder(View view) {
    super(view);
  }

  @Override
  public CalendarHeaderDaySingleBinding bind(Header calendar, Context context) {
    return null;
  }

}
