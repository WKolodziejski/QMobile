package com.tinf.qmobile.model.journal;

import static com.tinf.qmobile.model.ViewType.FOOTER_PERIOD;

import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.matter.Period;

public class FooterPeriod implements Queryable {
  public Period period;

  public FooterPeriod(Period period) {
    this.period = period;
  }

  @Override
  public int getItemType() {
    return FOOTER_PERIOD;
  }

  @Override
  public long getId() {
    return period.id;
  }

  @Override
  public boolean isSame(Queryable queryable) {
    return queryable.equals(this);
  }

}
