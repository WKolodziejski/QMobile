package com.tinf.qmobile.model.journal;

import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.matter.Period;

import static com.tinf.qmobile.model.Queryable.ViewType.PERIOD;

public class Header implements Queryable {
    private Period period;

    public Header(Period period) {
        this.period = period;
    }

    @Override
    public int getItemType() {
        return PERIOD;
    }

    public Period getPeriod() {
        return period;
    }

}
