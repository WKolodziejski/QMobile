package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.model.Queryable;

import butterknife.ButterKnife;

public abstract class SearchViewHolder<T extends Queryable> extends RecyclerView.ViewHolder {

    public SearchViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public abstract void bind(T item, Context context);

}
