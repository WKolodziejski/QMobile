package com.tinf.qmobile.widget.calendar.view.comparators;


import com.tinf.qmobile.model.calendar.Event;

import java.util.Comparator;

public class EventComparator implements Comparator<Event> {

  @Override
  public int compare(Event lhs,
                     Event rhs) {
    return Long.compare(lhs.getTimeInMillis(), rhs.getTimeInMillis());
  }
}
