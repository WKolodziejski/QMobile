package com.tinf.qmobile.model.journal;

import static com.tinf.qmobile.model.ViewType.FOOTERJOURNAL;

import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.matter.Matter;

public class FooterJournal implements Queryable {
    private final Matter matter;
    private final int i;

    public FooterJournal(int i, Matter matter) {
        this.i = i;
        this.matter = matter;
    }

    @Override
    public int getItemType() {
        return FOOTERJOURNAL;
    }

    public Matter getMatter() {
        return matter;
    }

    public int getPosition() {
        return i;
    }

    @Override
    public long getId() {
        return getMatter().id;
    }

    @Override
    public boolean isSame(Queryable queryable) {
        return queryable.equals(this);
    }

}
