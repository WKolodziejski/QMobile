package com.tinf.qmobile.model;

import static com.tinf.qmobile.model.ViewType.QUERY;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Query implements Queryable {
    @Id public long id;
    public String query;
    public long date;

    public Query() {}

    @Override
    public int getItemType() {
        return QUERY;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean isSame(Queryable queryable) {
        return queryable.equals(this);
    }

}
