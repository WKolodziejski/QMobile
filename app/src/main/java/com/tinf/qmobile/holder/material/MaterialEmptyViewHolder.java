package com.tinf.qmobile.holder.material;

import android.content.Context;
import android.view.ActionMode;
import android.view.View;

import androidx.annotation.NonNull;

import com.tinf.qmobile.adapter.MaterialsBaseAdapter;
import com.tinf.qmobile.adapter.OnInteractListener;
import com.tinf.qmobile.model.Empty;

public class MaterialEmptyViewHolder extends MaterialBaseViewHolder<Empty> {

    public MaterialEmptyViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(Context context, OnInteractListener listener, MaterialsBaseAdapter adapter, ActionMode.Callback callback, Empty empty, boolean isHeader) {

    }

}
