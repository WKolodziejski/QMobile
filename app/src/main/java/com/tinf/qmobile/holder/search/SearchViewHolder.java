package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.model.Query;
import com.tinf.qmobile.model.Query_;
import com.tinf.qmobile.model.Queryable;

import java.util.Date;

import butterknife.ButterKnife;
import io.objectbox.Box;

public abstract class SearchViewHolder<T extends Queryable> extends RecyclerView.ViewHolder {

    private static Box<Query> queryBox = DataBase.get().getBoxStore().boxFor(Query.class);

    public SearchViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public abstract void bind(T item, Context context, String query, SearchAdapter adapter);

    protected void saveQuery(String query) {
        Query q = queryBox.query().equal(Query_.query, query).build().findUnique();

        if (q == null)
            q = new Query();

        q.query = query;
        q.date = new Date().getTime();

        queryBox.put(q);
    }

}
