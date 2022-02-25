package com.tinf.qmobile.database;

import com.tinf.qmobile.model.Queryable;

import java.util.List;

public interface OnData<T> {
    void onUpdate(List<T> list);
}
