package com.tinf.qmobile.model.journal;

import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.matter.Period;

import static com.tinf.qmobile.model.Queryable.ViewType.FOOTERP;

public class FooterPeriod implements Queryable {
    private Period period;

    public FooterPeriod(Period period) {
        this.period = period;
    }

    @Override
    public int getItemType() {
        return FOOTERP;
    }

    public Period getPeriod() {
        return period;
    }

}
