package com.tinf.qmobile.adapter;

import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.FOOTERJ;
import static com.tinf.qmobile.model.ViewType.FOOTERP;
import static com.tinf.qmobile.model.ViewType.HEADER;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.JOURNALEMPTY;
import static com.tinf.qmobile.model.ViewType.PERIOD;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.database.OnData;
import com.tinf.qmobile.holder.journal.JournalBaseViewHolder;
import com.tinf.qmobile.holder.journal.JournalEmptyViewHolder;
import com.tinf.qmobile.holder.journal.JournalFooterViewHolder;
import com.tinf.qmobile.holder.journal.JournalHeaderViewHolder;
import com.tinf.qmobile.holder.journal.JournalViewHolder;
import com.tinf.qmobile.holder.journal.PeriodFooterViewHolder;
import com.tinf.qmobile.holder.journal.PeriodHeaderViewHolder;
import com.tinf.qmobile.model.Queryable;

import java.util.List;

public class JournalsAdapter extends RecyclerView.Adapter<JournalBaseViewHolder> implements OnData<Queryable> {
    private final AsyncListDiffer<Queryable> list;
    private final Context context;

    public JournalsAdapter(Context context) {
        this.context = context;
        this.list = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<Queryable>() {
            @Override
            public boolean areItemsTheSame(@NonNull Queryable oldItem, @NonNull Queryable newItem) {
                return oldItem.getId() == newItem.getId() && oldItem.getItemType() == newItem.getItemType();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Queryable oldItem, @NonNull Queryable newItem) {
                return oldItem.isSame(newItem);
            }
        });

        onUpdate(DataBase.get().getJournalsDataProvider().getList());
    }

    @NonNull
    @Override
    public JournalBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                return new JournalHeaderViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.journal_header, parent, false));

            case JOURNAL:
                return new JournalViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.journal_item, parent, false));

            case FOOTERJ:
                return new JournalFooterViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.journal_footer, parent, false));

            case FOOTERP:
                return new PeriodFooterViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.period_footer, parent, false));

            case PERIOD:
                return new PeriodHeaderViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.period_header, parent, false));

            case JOURNALEMPTY:
                return new JournalEmptyViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.journal_item_empty, parent, false));

            case EMPTY:
                return new JournalEmptyViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.journal_empty, parent, false));
        }

        return null;
    }

    @Override
    public int getItemViewType(int i) {
        return list.getCurrentList().get(i).getItemType();
    }

    @Override
    public void onBindViewHolder(@NonNull JournalBaseViewHolder holder, int i) {
        holder.bind(context, list.getCurrentList().get(i), true);
    }

    @Override
    public int getItemCount() {
        return list.getCurrentList().size();
    }

    @Override
    public void onUpdate(List<Queryable> list) {
        this.list.submitList(DataBase.get().getJournalsDataProvider().getList());
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        DataBase.get().getJournalsDataProvider().removeOnDataListener(this);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        DataBase.get().getJournalsDataProvider().addOnDataListener(this);
    }

}
