package com.tinf.qmobile.holder.material;

import android.content.Context;
import android.view.ActionMode;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.adapter.MaterialsAdapter;
import com.tinf.qmobile.model.Queryable;

import butterknife.ButterKnife;

public abstract class MaterialBaseViewHolder<T extends Queryable> extends RecyclerView.ViewHolder {

    public MaterialBaseViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public abstract void bind(Context context, MaterialsAdapter.OnInteractListener listener, MaterialsAdapter adapter, ActionMode.Callback callback, T t);

}
