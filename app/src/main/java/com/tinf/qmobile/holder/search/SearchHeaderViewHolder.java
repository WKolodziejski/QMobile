package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.databinding.SearchHeaderBinding;
import com.tinf.qmobile.model.search.Header;

public class SearchHeaderViewHolder extends SearchViewHolder<Header> {
    private final SearchHeaderBinding binding;

    public SearchHeaderViewHolder(@NonNull View view) {
        super(view);
        binding = SearchHeaderBinding.bind(view);
    }

    @Override
    public void bind(Header header, Context context, String query, SearchAdapter adapter) {
        binding.title.setText(header.getTitle());
    }

}
