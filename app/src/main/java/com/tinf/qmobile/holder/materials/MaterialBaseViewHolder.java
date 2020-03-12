package com.tinf.qmobile.holder.materials;

import android.content.Context;
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

    public abstract void bind(Context context, T t, MaterialsAdapter.OnInteractListener onDownload, MaterialsAdapter adapter);

}
