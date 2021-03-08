package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;

import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.model.Empty;

public class SearchEmptyViewHolder extends SearchViewHolder<Empty>  {

    public SearchEmptyViewHolder(@NonNull View view) {
        super(view);
    }

    @Override
    public void bind(Empty item, Context context, String query, SearchAdapter adapter) {

    }

}
