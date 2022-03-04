package com.tinf.qmobile.adapter;

import android.content.Context;
import android.os.Bundle;
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
import com.tinf.qmobile.holder.clazz.ClassBaseViewHolder;
import com.tinf.qmobile.holder.clazz.ClassEmptyViewHolder;
import com.tinf.qmobile.holder.clazz.ClassHeaderViewHolder;
import com.tinf.qmobile.holder.clazz.ClassItemViewHolder;
import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.matter.Clazz;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

import static com.tinf.qmobile.model.ViewType.CLASS;
import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.PERIOD;

public class ClassAdapter extends RecyclerView.Adapter<ClassBaseViewHolder> {
    private final Context context;
    private final AsyncListDiffer<Queryable> list;
    private final DataSubscription sub1;
    private final DataSubscription sub2;
    private final Handler handler;

    public ClassAdapter(Context context, Bundle bundle) {
        this.context = context;
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

        updateList(bundle);

        DataObserver observer = data -> updateList(bundle);

        sub1 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);

        sub2 = DataBase.get().getBoxStore().subscribe(Clazz.class)
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);
    }

    private void updateList(Bundle bundle) {
        DataBase.get().execute(() -> {
            List<Queryable> list = getList(bundle);

            handler.post(() -> this.list.submitList(list));
        });
    }

    private List<Queryable> getList(Bundle bundle) {
        ArrayList<Queryable> list = new ArrayList<>();

        Matter matter = DataBase.get().getBoxStore()
                .boxFor(Matter.class)
                .get(bundle.getLong("ID"));

        for (int i = 0; i < matter.periods.size(); i++) {
            Period p = matter.periods.get(i);

            if (!p.classes.isEmpty()) {
                list.add(p);
                List<Clazz> cls = p.classes;
                //Collections.reverse(cls);
                list.addAll(cls);
            }
        }

        /*for (Period p : matter.periods)
            if (!p.classes.isEmpty()) {
                list.add(p);
                List<Clazz> cls = p.classes;
                Collections.reverse(cls);
                list.addAll(cls);
            }*/

        if (list.isEmpty())
            list.add(new Empty());

        return list;
    }

    @NonNull
    @Override
    public ClassBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CLASS:
                return new ClassItemViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.class_item, parent, false));

            case PERIOD:
                return new ClassHeaderViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.class_header, parent, false));

            case EMPTY:
                return new ClassEmptyViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.class_empty, parent, false));
        }

        return null;
    }

    @Override
    public int getItemViewType(int i) {
        return list.getCurrentList().get(i).getItemType();
    }

    @Override
    public void onBindViewHolder(@NonNull ClassBaseViewHolder holder, int i) {
        holder.bind(context, list.getCurrentList().get(i));
    }

    @Override
    public int getItemCount() {
        return list.getCurrentList().size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        sub1.cancel();
        sub2.cancel();
    }

    /*public int highlight(long id) {
        for (int i = 0; i < classes.getCurrentList().size(); i++) {
            Queryable q = classes.getCurrentList().get(i);

            if (q instanceof Clazz) {
                Clazz c = (Clazz) q;

                if (c.id == id) {
                    c.highlight = true;
                    notifyItemChanged(i);
                    return i;
                }
            }
        }

        return -1;
    }*/

}
