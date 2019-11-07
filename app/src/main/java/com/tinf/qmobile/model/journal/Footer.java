package com.tinf.qmobile.model.journal;

import com.tinf.qmobile.model.matter.Matter;

import static com.tinf.qmobile.model.journal.JournalBase.ViewType.FOOTER;

public class Footer implements JournalBase {
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
