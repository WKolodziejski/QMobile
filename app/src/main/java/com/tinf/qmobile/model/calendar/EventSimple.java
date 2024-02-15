package com.tinf.qmobile.model.calendar;

import static com.tinf.qmobile.model.ViewType.SIMPLE;
import static com.tinf.qmobile.model.calendar.EventSimple.Type.FIM;
import static com.tinf.qmobile.model.calendar.EventSimple.Type.INICIO;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.model.Queryable;

import java.util.Objects;

import io.objectbox.annotation.Entity;

@Entity
public class EventSimple extends Event {
  public enum Type {
    INICIO(1),
    FIM(2);

    private final int i;

    Type(final int i) {
      this.i = i;
    }

    public int get() {
      return i;
    }
  }

  private int type;

  public EventSimple(String title, long startTime) {
    super(title, startTime);
  }

  public EventSimple(String title, long startTime, long endTime) {
    super(title, startTime, endTime);
  }

  public EventSimple(int type, long startTime) {
    super(type == INICIO.get() ? App.getContext().getString(R.string.calendar_period_start)
                               : App.getContext().getString(R.string.calendar_period_end),
          startTime);
    this.type = type;
  }

  @Override
  public String getTitle() {
    if (type == INICIO.get()) {
      return App.getContext().getString(R.string.calendar_period_start);
    } else if (type == FIM.get()) {
      return App.getContext().getString(R.string.calendar_period_end);
    } else return super.getTitle();
  }

  /*
   * Required methods
   */

  public EventSimple() {
    super();
  }

  public int getType() {
    return type;
  }

  @Override
  public int getItemType() {
    return SIMPLE;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EventSimple)) return false;
    if (!super.equals(o)) return false;
    EventSimple that = (EventSimple) o;
    return getType() == that.getType();
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getType());
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public boolean isSame(Queryable queryable) {
    return queryable.equals(this);
  }

}
