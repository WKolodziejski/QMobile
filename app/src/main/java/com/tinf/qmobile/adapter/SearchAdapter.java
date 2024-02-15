package com.tinf.qmobile.adapter;

import static com.tinf.qmobile.model.ViewType.CLASS;
import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.HEADERSEARCH;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MATERIAL;
import static com.tinf.qmobile.model.ViewType.MATTER;
import static com.tinf.qmobile.model.ViewType.MESSAGE;
import static com.tinf.qmobile.model.ViewType.QUERY;
import static com.tinf.qmobile.model.ViewType.SIMPLE;
import static com.tinf.qmobile.model.ViewType.USER;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.holder.search.SearchClassViewHolder;
import com.tinf.qmobile.holder.search.SearchEmptyViewHolder;
import com.tinf.qmobile.holder.search.SearchEventViewHolder;
import com.tinf.qmobile.holder.search.SearchHeaderViewHolder;
import com.tinf.qmobile.holder.search.SearchJournalViewHolder;
import com.tinf.qmobile.holder.search.SearchMaterialViewHolder;
import com.tinf.qmobile.holder.search.SearchMatterViewHolder;
import com.tinf.qmobile.holder.search.SearchMessageViewHolder;
import com.tinf.qmobile.holder.search.SearchQueryViewHolder;
import com.tinf.qmobile.holder.search.SearchViewHolder;
import com.tinf.qmobile.model.Query;
import com.tinf.qmobile.model.Query_;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.parser.SearchParser;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
  private final Context context;
  private List<Queryable> list;
  private final SearchParser parser;
  private String query;

  public SearchAdapter(Context context) {
    this.context = context;
    this.list = new ArrayList<>();
    this.parser = new SearchParser(context, list -> {
      this.list = list;
      notifyDataSetChanged();
    });

    showRecentQueries();
  }

  public void query(String query) {
    this.query = query;

    if (query.isEmpty()) {
      showRecentQueries();
      return;
    }

    parser.execute(query);
  }

  public interface OnQuery {
    void onQuery(String query);
  }

  private OnQuery onQuery;

  public void requestHideKeyboard(String query) {
    onQuery.onQuery(query);
  }

  public void setOnQueryListener(OnQuery onQuery) {
    this.onQuery = onQuery;
  }

  private void showRecentQueries() {
    list.clear();
    list.addAll(DataBase
                    .get()
                    .getBoxStore()
                    .boxFor(Query.class)
                    .query()
                    .orderDesc(Query_.date)
                    .build()
                    .find(0, 5));

    notifyDataSetChanged();
  }

  @Override
  public int getItemViewType(int i) {
    return list.get(i).getItemType();
  }

  @NonNull
  @Override
  public SearchViewHolder onCreateViewHolder(
      @NonNull
      ViewGroup parent, int viewType) {
    switch (viewType) {
      case JOURNAL:
        return new SearchJournalViewHolder(LayoutInflater.from(context)
                                                         .inflate(R.layout.search_journal, parent,
                                                                  false));

      case MATERIAL:
        return new SearchMaterialViewHolder(LayoutInflater.from(context)
                                                          .inflate(R.layout.search_material, parent,
                                                                   false));

      case MATTER:
        return new SearchMatterViewHolder(LayoutInflater.from(context)
                                                        .inflate(R.layout.search_matter, parent,
                                                                 false));

      case MESSAGE:
        return new SearchMessageViewHolder(LayoutInflater.from(context)
                                                         .inflate(R.layout.search_message, parent,
                                                                  false));

      case SIMPLE:
      case USER:
        return new SearchEventViewHolder(LayoutInflater.from(context)
                                                       .inflate(R.layout.search_event, parent,
                                                                false));

      case CLASS:
        return new SearchClassViewHolder(LayoutInflater.from(context)
                                                       .inflate(R.layout.search_class, parent,
                                                                false));

      case HEADERSEARCH:
        return new SearchHeaderViewHolder(LayoutInflater.from(context)
                                                        .inflate(R.layout.search_header, parent,
                                                                 false));

      case EMPTY:
        return new SearchEmptyViewHolder(LayoutInflater.from(context)
                                                       .inflate(R.layout.search_empty, parent,
                                                                false));

      case QUERY:
        return new SearchQueryViewHolder(LayoutInflater.from(context)
                                                       .inflate(R.layout.search_query, parent,
                                                                false));
    }

    return null;
  }

  @Override
  public void onBindViewHolder(
      @NonNull
      SearchViewHolder holder, int i) {
    holder.bind(list.get(i), context, query, this);
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  public List<Queryable> getList() {
    return list;
  }

}
