package com.tinf.qmobile.model.journal;

import static com.tinf.qmobile.model.ViewType.HEADER;

import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.matter.Matter;

public class Header implements Queryable {
  private final Matter matter;

  public Header(Matter matter) {
    this.matter = matter;
  }

  public int getJournalNotSeenCount() {
    return matter.getJournalNotSeenCount();
  }

  public int getColor() {
    return matter.getColor();
  }

  @Override
  public int getItemType() {
    return HEADER;
  }

  @Override
  public long getId() {
    return matter.id;
  }

  @Override
  public boolean isSame(Queryable queryable) {
    return queryable.equals(this);
  }

}
