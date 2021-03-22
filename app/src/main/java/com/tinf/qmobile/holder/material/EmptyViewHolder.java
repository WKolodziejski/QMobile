package com.tinf.qmobile.holder.material;

import android.content.Context;
import android.view.ActionMode;
import android.view.View;

import androidx.annotation.NonNull;

import com.tinf.qmobile.adapter.MaterialsAdapter;
import com.tinf.qmobile.model.Empty;

public class EmptyViewHolder extends MaterialBaseViewHolder<Empty> {

    public EmptyViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(Context context, MaterialsAdapter.OnInteractListener listener, MaterialsAdapter adapter, ActionMode.Callback callback, Empty empty) {

    }

}
