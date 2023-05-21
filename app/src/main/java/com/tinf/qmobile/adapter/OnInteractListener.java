package com.tinf.qmobile.adapter;

import android.view.ActionMode;
import android.view.Menu;

public interface OnInteractListener {
  boolean isSelectionMode();

  void setSelectionMode(ActionMode.Callback callback);

  void onSelectedCount(int size);

  boolean onCreateActionMode(ActionMode actionMode, Menu menu);

  void onDestroyActionMode(ActionMode actionMode);
}
