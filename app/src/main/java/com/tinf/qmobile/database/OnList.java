package com.tinf.qmobile.database;

import com.tinf.qmobile.model.calendar.EventBase;

import java.util.List;

public interface OnList<T> {
    void onUpdate(List<T> list);
}
