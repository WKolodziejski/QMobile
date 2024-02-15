package com.tinf.qmobile.model.search;

import static com.tinf.qmobile.model.ViewType.HEADER_SEARCH;

import com.tinf.qmobile.model.Queryable;

public class Header implements Queryable {
  private final String title;

  public Header(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public int getItemType() {
    return HEADER_SEARCH;
  }

  @Override
  public long getId() {
    return HEADER_SEARCH;
  }

  @Override
  public boolean isSame(Queryable queryable) {
    return queryable.equals(this);
  }

}
