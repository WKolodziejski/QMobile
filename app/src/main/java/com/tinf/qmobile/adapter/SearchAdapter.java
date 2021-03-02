package com.tinf.qmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.holder.journal.JournalViewHolder;
import com.tinf.qmobile.holder.material.MaterialViewHolder;
import com.tinf.qmobile.holder.message.MessageViewHolder;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.message.Attachment;
import com.tinf.qmobile.model.message.Message;
import java.util.List;
import static com.tinf.qmobile.model.ViewType.ATTACHMENT;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MATERIAL;
import static com.tinf.qmobile.model.ViewType.MESSAGE;

public class SearchAdapter extends BaseAdapter {
    private Context context;
    private List<Queryable> list;

    public SearchAdapter(Context context, List<Queryable> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Queryable getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        Queryable item = list.get(i);

        switch (item.getItemType()) {
            case JOURNAL:
                return ((Journal) item).id;

            case MATERIAL:
                return ((Material) item).id;

            case MESSAGE:
                return ((Message) item).id;

            case ATTACHMENT:
                return ((Attachment) item).id;
        }

        return -1;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        Queryable item = list.get(i);

        switch (item.getItemType()) {
            case JOURNAL:
                JournalViewHolder holder1 = new JournalViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.journal_item, parent, false));
                holder1.bind(context, (Journal) item);
                return holder1.itemView;

            case MATERIAL:
                MaterialViewHolder holder2 = new MaterialViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.material_item, parent, false));
                holder2.bind(context, (Material) item);
                return holder2.itemView;

            case MESSAGE:
                MessageViewHolder holder3 = new MessageViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.message_header, parent, false));
                holder3.bind(context, (Message) item);
                return holder3.itemView;

            case ATTACHMENT:
                break;
        }

        return null;
    }

}
