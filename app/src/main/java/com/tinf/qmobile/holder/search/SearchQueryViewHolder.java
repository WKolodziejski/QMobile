package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.model.Query;

import butterknife.BindView;

public class SearchQueryViewHolder extends SearchViewHolder<Query>  {
    @BindView(R.id.search_query_title)      TextView title;

    public SearchQueryViewHolder(@NonNull View view) {
        super(view);
    }

    @Override
    public void bind(Query item, Context context, String query, SearchAdapter adapter) {
        title.setText(item.query);
        itemView.setOnClickListener(view -> {
            adapter.requestHideKeyboard(item.query);
            adapter.query(item.query);
        });
    }

}
