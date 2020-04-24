package com.tinf.qmobile.model.message;

import com.tinf.qmobile.model.Queryable;
import static com.tinf.qmobile.model.Queryable.ViewType.EMPTY;

public class Empty implements Queryable {

    @Override
    public int getItemType() {
        return EMPTY;
    }

}
