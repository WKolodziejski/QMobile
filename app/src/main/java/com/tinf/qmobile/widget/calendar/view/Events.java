package com.tinf.qmobile.widget.calendar.view;

import androidx.annotation.NonNull;

import com.tinf.qmobile.model.calendar.Event;

import java.util.List;
import java.util.Objects;

class Events {

  private final List<Event> events;
  private final long timeInMillis;

  Events(long timeInMillis,
         List<Event> events) {
    this.timeInMillis = timeInMillis;
    this.events = events;
  }

  long getTimeInMillis() {
    return timeInMillis;
  }

  List<Event> getEvents() {
    return events;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Events event = (Events) o;

    if (timeInMillis != event.timeInMillis) return false;
    return Objects.equals(events, event.events);
  }

  @Override
  public int hashCode() {
    int result = events != null ? events.hashCode() : 0;
    result = 31 * result + (int) (timeInMillis ^ (timeInMillis >>> 32));
    return result;
  }

  @NonNull
  @Override
  public String toString() {
    return "Events{" +
           "events=" + events +
           ", timeInMillis=" + timeInMillis +
           '}';
  }
}
