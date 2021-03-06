package com.tinf.qmobile.model;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.tinf.qmobile.model.Queryable.ViewType.EMPTY;
import static com.tinf.qmobile.model.Queryable.ViewType.FOOTERP;
import static com.tinf.qmobile.model.Queryable.ViewType.FOOTERJ;
import static com.tinf.qmobile.model.Queryable.ViewType.HEADER;
import static com.tinf.qmobile.model.Queryable.ViewType.JOURNAL;
import static com.tinf.qmobile.model.Queryable.ViewType.MATERIAL;

public interface Queryable {

    @IntDef({HEADER, JOURNAL, MATERIAL, FOOTERP, FOOTERJ, EMPTY})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
        int HEADER = 100;
        int JOURNAL = 200;
        int MATERIAL = 300;
        int PERIOD = 400;
        int FOOTERJ = 900;
        int FOOTERP = 910;
        int EMPTY = 500;
    }

    int getItemType();

}
