package com.tinf.qmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.holder.journal.JournalViewHolder;
import com.tinf.qmobile.holder.material.MaterialViewHolder;
import com.tinf.qmobile.holder.message.MessageViewHolder;
import com.tinf.qmobile.holder.search.SearchClassViewHolder;
import com.tinf.qmobile.holder.search.SearchEventViewHolder;
import com.tinf.qmobile.holder.search.SearchJournalViewHolder;
import com.tinf.qmobile.holder.search.SearchMaterialViewHolder;
import com.tinf.qmobile.holder.search.SearchMatterViewHolder;
import com.tinf.qmobile.holder.search.SearchMessageViewHolder;
import com.tinf.qmobile.holder.search.SearchViewHolder;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.message.Attachment;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.parser.SearchParser;

import java.util.ArrayList;
import java.util.List;
import static com.tinf.qmobile.model.ViewType.ATTACHMENT;
import static com.tinf.qmobile.model.ViewType.CLASS;
import static com.tinf.qmobile.model.ViewType.HEADER;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MATERIAL;
import static com.tinf.qmobile.model.ViewType.MESSAGE;
import static com.tinf.qmobile.model.ViewType.SIMPLE;
import static com.tinf.qmobile.model.ViewType.USER;

public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    private Context context;
    private List<Queryable> list;
    //private OnQuery onQuery;

    public interface OnQuery {
        void onQuery(long id, long id2, int type);
    }

    public SearchAdapter(Context context, String query) {//, OnQuery onQuery) {
        this.context = context;
        //this.onQuery = onQuery;
        this.list = new ArrayList<>();

        new SearchParser(list -> {
            this.list = list;
            notifyDataSetChanged();
        }).execute(query);
    }

    @Override
    public int getItemViewType(int i) {
        return list.get(i).getItemType();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case JOURNAL:
                return new SearchJournalViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.search_journal, parent, false));

            case MATERIAL:
                return new SearchMaterialViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.search_material, parent, false));

            case HEADER:
                return new SearchMatterViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.search_matter, parent, false));

            case MESSAGE:
                return new SearchMessageViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.search_message, parent, false));

            case SIMPLE:
            case USER:
                return new SearchEventViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.search_event, parent, false));

            case CLASS:
                return new SearchClassViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.search_class, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int i) {
        holder.bind(list.get(i), context);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
