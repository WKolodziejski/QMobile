package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.databinding.SearchQueryBinding;
import com.tinf.qmobile.model.Query;

public class SearchQueryViewHolder extends SearchViewHolder<Query>  {
    private final SearchQueryBinding binding;

    public SearchQueryViewHolder(@NonNull View view) {
        super(view);
        binding = SearchQueryBinding.bind(view);
    }

    @Override
    public void bind(Query item, Context context, String query, SearchAdapter adapter) {
        binding.title.setText(item.query);
        itemView.setOnClickListener(view -> {
            adapter.requestHideKeyboard(item.query);
            adapter.query(item.query);
        });
    }

}
