package com.tinf.qmobile.holder.material;

import android.content.Context;
import android.view.ActionMode;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.adapter.MaterialsBaseAdapter;
import com.tinf.qmobile.adapter.OnMaterialInteractListener;
import com.tinf.qmobile.model.Queryable;

public abstract class MaterialBaseViewHolder<T extends Queryable> extends RecyclerView.ViewHolder {

  public MaterialBaseViewHolder(
      @NonNull
      View view) {
    super(view);
  }

  public abstract void bind(Context context, OnMaterialInteractListener listener,
                            MaterialsBaseAdapter adapter, ActionMode.Callback callback, T t,
                            boolean isHeader);

}
