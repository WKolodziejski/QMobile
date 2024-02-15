package com.tinf.qmobile.holder.material;

import android.content.Context;
import android.view.ActionMode;
import android.view.View;

import com.tinf.qmobile.adapter.MaterialsBaseAdapter;
import com.tinf.qmobile.adapter.OnMaterialInteractListener;
import com.tinf.qmobile.model.journal.HeaderMatter;

public class MaterialHeaderColorViewHolder extends MaterialBaseViewHolder<HeaderMatter> {

  public MaterialHeaderColorViewHolder(View view) {
    super(view);
  }

  @Override
  public void bind(Context context, OnMaterialInteractListener listener, MaterialsBaseAdapter adapter,
                   ActionMode.Callback callback, HeaderMatter header, boolean isHeader) {

  }
}
