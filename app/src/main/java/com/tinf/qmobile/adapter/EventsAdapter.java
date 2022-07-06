package com.tinf.qmobile.adapter;

import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.SIMPLE;
import static com.tinf.qmobile.model.ViewType.USER;

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
import com.tinf.qmobile.database.OnList;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.holder.calendar.horizontal.EmptyViewHolder;
import com.tinf.qmobile.holder.calendar.horizontal.EventJournalHorizontalViewHolder;
import com.tinf.qmobile.holder.calendar.horizontal.EventSimpleHorizontalViewHolder;
import com.tinf.qmobile.holder.calendar.horizontal.EventUserHorizontalViewHolder;
import com.tinf.qmobile.model.calendar.EventBase;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<CalendarViewHolder> implements OnData<EventBase> {
    private final Context context;
    private final AsyncListDiffer<EventBase> list;
    private final OnList<EventBase> onList;

    public EventsAdapter(Context context, OnList<EventBase> onList) {
        this.context = context;
        this.onList = onList;
        this.list = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<EventBase>() {
            @Override
            public boolean areItemsTheSame(@NonNull EventBase oldItem, @NonNull EventBase newItem) {
                return oldItem.getId() == newItem.getId() && oldItem.getItemType() == newItem.getItemType();
            }

            @Override
            public boolean areContentsTheSame(@NonNull EventBase oldItem, @NonNull EventBase newItem) {
                return oldItem.isSame(newItem);
            }
        });

        onUpdate(DataBase.get().getEventsDataProvider().getList());
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case JOURNAL:
                return new EventJournalHorizontalViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_event_journal_h, parent, false));

            case SIMPLE:
                return new EventSimpleHorizontalViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_event_simple_h, parent, false));

            case USER:
                return new EventUserHorizontalViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_event_user_h, parent, false));
//
//            case EMPTY:
//                return new EmptyViewHolder(LayoutInflater.from(context)
//                        .inflate(R.layout.header_empty, parent, false));
        }
        return null;
    }

    @Override
    public int getItemViewType(int i) {
        return list.getCurrentList().get(i).getItemType();
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int i) {
        holder.bind(list.getCurrentList().get(i), context);
    }

    @Override
    public int getItemCount() {
        return list.getCurrentList().size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        DataBase.get().getEventsDataProvider().addOnDataListener(this);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        DataBase.get().getEventsDataProvider().removeOnDataListener(this);
    }

    @Override
    public void onUpdate(List<EventBase> list) {
        this.list.submitList(list);
        onList.onUpdate(list);
    }

}
