package com.tinf.qmobile.model.journal;

import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.matter.Matter;

import static com.tinf.qmobile.model.ViewType.FOOTERJ;

public class FooterJournal implements Queryable {
    private final Matter matter;
    private final int i;

    public FooterJournal(int i, Matter matter) {
        this.i = i;
        this.matter = matter;
    }

    @Override
    public int getItemType() {
        return FOOTERJ;
    }

    public Matter getMatter() {
        return matter;
    }

    public int getPosition() {
        return i;
    }

}
