package com.tinf.qmobile.holder.clazz;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tinf.qmobile.model.Queryable;

public abstract class ClassBaseViewHolder<T extends Queryable> extends RecyclerView.ViewHolder {

    public ClassBaseViewHolder(@NonNull View view) {
        super(view);
    }

    public abstract void bind(Context context, T t);

}
