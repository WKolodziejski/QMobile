package com.tinf.qmobile.database;

import java.util.List;

public interface OnData<T> {
  void onUpdate(List<T> list);
}
