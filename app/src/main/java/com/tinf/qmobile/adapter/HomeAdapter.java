package com.tinf.qmobile.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.database.OnData;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.holder.calendar.horizontal.EmptyViewHolder;
import com.tinf.qmobile.holder.calendar.horizontal.EventJournalHorizontalViewHolder;
import com.tinf.qmobile.holder.calendar.horizontal.EventSimpleHorizontalViewHolder;
import com.tinf.qmobile.holder.calendar.horizontal.EventUserHorizontalViewHolder;
import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.model.calendar.EventSimple;
import com.tinf.qmobile.model.calendar.EventSimple_;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.calendar.EventUser_;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.journal.Journal_;
import com.tinf.qmobile.model.matter.Matter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import io.objectbox.Box;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.SIMPLE;
import static com.tinf.qmobile.model.ViewType.USER;

public class HomeAdapter extends RecyclerView.Adapter<CalendarViewHolder> implements OnData<CalendarBase> {
    private final Context context;
    private final AsyncListDiffer<CalendarBase> list;

    public HomeAdapter(Context context) {
        this.context = context;
        this.list = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<CalendarBase>() {
            @Override
            public boolean areItemsTheSame(@NonNull CalendarBase oldItem, @NonNull CalendarBase newItem) {
                return oldItem.getId() == newItem.getId() && oldItem.getItemType() == newItem.getItemType();
            }

            @Override
            public boolean areContentsTheSame(@NonNull CalendarBase oldItem, @NonNull CalendarBase newItem) {
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

            case EMPTY:
                return new EmptyViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_empty, parent, false));
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
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        DataBase.get().getEventsDataProvider().removeOnDataListener(this);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        DataBase.get().getEventsDataProvider().addOnDataListener(this);
    }

    @Override
    public void onUpdate(List<CalendarBase> list) {
        this.list.submitList(list);
    }

}
