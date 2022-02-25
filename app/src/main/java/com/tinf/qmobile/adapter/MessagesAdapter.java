package com.tinf.qmobile.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.holder.message.EmptyViewHolder;
import com.tinf.qmobile.holder.message.MessageViewHolder;
import com.tinf.qmobile.holder.message.MessagesViewHolder;
import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.model.message.Message_;
import com.tinf.qmobile.network.message.Messenger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.MESSAGE;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesViewHolder> {
    private final Context context;
    private final AsyncListDiffer<Queryable> list;
    private final Messenger messenger;
    private final DataSubscription sub1;
    private final Handler handler;

    public MessagesAdapter(Context context, Messenger messenger) {
        this.context = context;
        this.messenger = messenger;
        this.handler = new Handler(Looper.getMainLooper());
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

        updateList();

        DataObserver observer = data -> updateList();

        sub1 = DataBase.get().getBoxStore().subscribe(Message.class)
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);
    }

    private void updateList() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Queryable> list = getList();
            handler.post(() -> this.list.submitList(list));
        });
    }

    private List<Queryable> getList() {
        List<Queryable> list = new ArrayList<>(
                DataBase.get().getBoxStore()
                .boxFor(Message.class)
                .query()
                .orderDesc(Message_.date_)
                .build()
                .find());

        if (list.isEmpty())
            list.add(new Empty());

        return list;
    }

    @Override
    public int getItemViewType(int i) {
        return list.getCurrentList().get(i).getItemType();
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case MESSAGE:
                return new MessageViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.message_header, parent, false));

            case EMPTY:
                return new EmptyViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.message_empty, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int i) {
        holder.bind(context, messenger, list.getCurrentList().get(i));
    }

    @Override
    public int getItemCount() {
        return list.getCurrentList().size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        sub1.cancel();
    }

}
