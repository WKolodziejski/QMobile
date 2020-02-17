package com.tinf.qmobile.model.journal;

import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.matter.Matter;
import static com.tinf.qmobile.model.Queryable.ViewType.FOOTER;

public class Footer implements Queryable {
    private Matter matter;
    private int i;

    public Footer(int i, Matter matter) {
        this.i = i;
        this.matter = matter;
    }

    @Override
    public int getItemType() {
        return FOOTER;
    }

    public Matter getMatter() {
        return matter;
    }

    public int getPosition() {
        return i;
    }

}
