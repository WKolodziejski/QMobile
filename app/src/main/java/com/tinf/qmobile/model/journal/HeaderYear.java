package com.tinf.qmobile.model.journal;

import static com.tinf.qmobile.model.ViewType.HEADER_YEAR;

import com.tinf.qmobile.model.Queryable;

public class HeaderYear implements Queryable {
  private final String year;

  public HeaderYear(String year) {
    this.year = year;
  }

  @Override
  public int getItemType() {
    return HEADER_YEAR;
  }

  @Override
  public long getId() {
    return -1;
  }

  @Override
  public boolean isSame(Queryable queryable) {
    return queryable.equals(this);
  }

  public String getYear() {
    return year;
  }
}
