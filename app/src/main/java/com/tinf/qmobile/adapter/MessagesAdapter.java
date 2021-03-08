package com.tinf.qmobile.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.holder.message.EmptyViewHolder;
import com.tinf.qmobile.holder.message.MessageViewHolder;
import com.tinf.qmobile.holder.message.MessagesViewHolder;
import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.model.message.Message_;
import com.tinf.qmobile.network.message.Messenger;
import java.util.ArrayList;
import java.util.List;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;
import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.MESSAGE;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesViewHolder> {
    private List<Queryable> messages;
    private Context context;
    private Messenger messenger;
    private DataSubscription sub1;

    public MessagesAdapter(Context context, Messenger messenger, Bundle bundle) {
        this.context = context;
        this.messenger = messenger;

        messages = getList();

        DataObserver observer = data -> {
            List<Queryable> updated = getList();

            if (bundle != null) {
                for (int i = 0; i < messages.size(); i++) {
                    if (messages.get(i) instanceof Message) {
                        Message m1 = ((Message) messages.get(i));

                        for (Queryable q : updated) {
                            if (q instanceof Message) {
                                Message m2 = (Message) q;

                                if (m1.id == m2.id) {
                                    m2.highlight = m1.highlight;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

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
                    Queryable oldQ = messages.get(o);
                    Queryable newQ = updated.get(n);

                    if (oldQ instanceof Message && newQ instanceof Message)
                        return ((Message) oldQ).id == ((Message) newQ).id;

                    else return oldQ instanceof Empty && newQ instanceof Empty;
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

        sub1 = DataBase.get().getBoxStore().subscribe(Message.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e("Adapter", th.toString()))
                .observer(observer);
    }

    private List<Queryable> getList() {
        List<Queryable> list = new ArrayList<>(DataBase.get().getBoxStore()
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
        holder.bind(context, messenger, messages.get(i));
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

    public int highlight(long id) {
        for (int i = 0; i < messages.size(); i++) {
            Queryable q = messages.get(i);

            if (q instanceof Message) {
                Message m = (Message) q;

                if (m.id == id) {
                    m.highlight = true;
                    notifyItemChanged(i);
                    return i;
                }
            }
        }

        return -1;
    }

}
