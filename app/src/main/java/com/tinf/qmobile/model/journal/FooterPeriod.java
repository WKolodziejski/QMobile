package com.tinf.qmobile.model.journal;

import static com.tinf.qmobile.model.ViewType.FOOTERPERIOD;

import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.matter.Period;

public class FooterPeriod implements Queryable {
  public Period period;

  public FooterPeriod(Period period) {
    this.period = period;
  }

  @Override
  public int getItemType() {
    return FOOTERPERIOD;
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
