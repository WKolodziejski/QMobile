package com.tinf.qmobile.model.journal;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.matter.Period;

import static com.tinf.qmobile.model.ViewType.FOOTERP;

public class FooterPeriod implements Queryable {
    public Period period;

    public FooterPeriod(Period period) {
        this.period = period;
    }

    @Override
    public int getItemType() {
        return FOOTERP;
    }

}
