package com.tinf.qmobile.model.search;

import com.tinf.qmobile.model.Queryable;

import static com.tinf.qmobile.model.ViewType.HEADERSEARCH;

public class Header implements Queryable {
    private final String title;

    public Header(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int getItemType() {
        return HEADERSEARCH;
    }

    @Override
    public long getId() {
        return HEADERSEARCH;
    }

    @Override
    public boolean isSame(Queryable queryable) {
        return queryable.equals(this);
    }

}
