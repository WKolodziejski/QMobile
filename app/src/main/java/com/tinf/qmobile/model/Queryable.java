package com.tinf.qmobile.model;

public interface Queryable {

    int getItemType();
    long getId();
    boolean isSame(Queryable queryable);

}
