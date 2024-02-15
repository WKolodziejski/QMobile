package com.tinf.qmobile.holder.calendar.horizontal;

import android.content.Context;
import android.view.View;

import com.tinf.qmobile.databinding.CalendarHeaderDaySingleBinding;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.Empty;

public class EmptyViewHolder extends CalendarViewHolder<Empty> {

  public EmptyViewHolder(View view) {
    super(view);
  }

  @Override
  protected CalendarHeaderDaySingleBinding bind(Empty calendar,
                                                Context context) {
    return null;
  }

}
