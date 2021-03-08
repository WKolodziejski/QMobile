package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.Query;

import butterknife.BindView;

import static com.tinf.qmobile.model.ViewType.CLASS;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MATERIAL;
import static com.tinf.qmobile.model.ViewType.MESSAGE;

public class SearchQueryViewHolder extends SearchViewHolder<Query>  {
    @BindView(R.id.search_query_title)      TextView title;

    public SearchQueryViewHolder(@NonNull View view) {
        super(view);
    }

    @Override
    public void bind(Query item, Context context, String query, SearchAdapter adapter) {
        title.setText(item.query);
        itemView.setOnClickListener(view -> adapter.query(item.query));
    }

}
