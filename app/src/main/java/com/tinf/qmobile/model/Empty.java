package com.tinf.qmobile.model;

import static com.tinf.qmobile.model.ViewType.EMPTY;

import com.tinf.qmobile.model.calendar.EventBase;

public class Empty extends EventBase implements Queryable {
  private final int type;

  public Empty() {
    this.type = EMPTY;
  }

  public Empty(int type) {
    this.type = type;
  }

  @Override
  public int getItemType() {
    return type;
  }

  @Override
  public long getId() {
    return EMPTY;
  }

  @Override
  public boolean isSame(Queryable queryable) {
    return queryable.getItemType() == type;
  }

}
