package com.tinf.qmobile.model;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import static com.tinf.qmobile.model.Queryable.ViewType.FOOTER;
import static com.tinf.qmobile.model.Queryable.ViewType.HEADER;
import static com.tinf.qmobile.model.Queryable.ViewType.JOURNAL;
import static com.tinf.qmobile.model.Queryable.ViewType.MATERIAL;

public interface Queryable {

    @IntDef({HEADER, JOURNAL, MATERIAL, FOOTER})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
        int HEADER = 100;
        int JOURNAL = 200;
        int MATERIAL = 300;
        int PERIOD = 400;
        int FOOTER = 900;
    }

    int getItemType();

}
