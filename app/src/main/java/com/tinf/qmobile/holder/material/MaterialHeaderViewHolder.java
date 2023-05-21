package com.tinf.qmobile.holder.material;

import android.content.Context;
import android.view.ActionMode;
import android.view.View;

import com.tinf.qmobile.adapter.MaterialsBaseAdapter;
import com.tinf.qmobile.adapter.OnInteractListener;
import com.tinf.qmobile.model.journal.Header;

public class MaterialHeaderViewHolder extends MaterialBaseViewHolder<Header> {

  public MaterialHeaderViewHolder(View view) {
    super(view);
  }

  @Override
  public void bind(Context context, OnInteractListener listener, MaterialsBaseAdapter adapter,
                   ActionMode.Callback callback, Header header, boolean isHeader) {

  }
}
