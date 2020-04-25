package com.tinf.qmobile.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.holder.message.EmptyViewHolder;
import com.tinf.qmobile.holder.message.MessageViewHolder;
import com.tinf.qmobile.holder.message.MessagesViewHolder;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.message.Empty;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.model.message.Message_;
import java.util.ArrayList;
import java.util.List;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;
import static com.tinf.qmobile.model.Queryable.ViewType.EMPTY;
import static com.tinf.qmobile.model.Queryable.ViewType.MESSAGE;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesViewHolder> {
    private List<Queryable> messages;
    private Context context;
    private WebView webView;
    private DataSubscription sub1;

    public MessagesAdapter(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;

        BoxStore boxStore = DataBase.get().getBoxStore();

        messages = getList();

        DataObserver observer = data -> {
            List<Queryable> updated = getList();

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {

                @Override
                public int getOldListSize() {
                    return messages.size();
                }

                @Override
                public int getNewListSize() {
                    return updated.size();
                }

                @Override
                public boolean areItemsTheSame(int o, int n) {
                    if (messages.get(o) instanceof Message && messages.get(n) instanceof Message)
                        return ((Message) messages.get(o)).id == ((Message) messages.get(n)).id;

                    else return messages.get(o) instanceof Empty && updated.get(n) instanceof Empty;
                }

                @Override
                public boolean areContentsTheSame(int o, int n) {
                    return messages.get(o).equals(updated.get(n));
                }

            }, true);

            messages.clear();
            messages.addAll(updated);
            result.dispatchUpdatesTo(this);
        };

        sub1 = boxStore.subscribe(Message.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);
    }

    private List<Queryable> getList() {
        return new ArrayList<>(DataBase.get().getBoxStore()
                .boxFor(Message.class)
                .query()
                .orderDesc(Message_.date_)
                .build()
                .find());
    }

    @Override
    public int getItemViewType(int i) {
        return messages.get(i).getItemType();
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
        holder.bind(context, webView, messages.get(i));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        sub1.cancel();
    }

}
