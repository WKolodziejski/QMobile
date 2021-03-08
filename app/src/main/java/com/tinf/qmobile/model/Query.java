package com.tinf.qmobile.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import static com.tinf.qmobile.model.ViewType.QUERY;

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

}
