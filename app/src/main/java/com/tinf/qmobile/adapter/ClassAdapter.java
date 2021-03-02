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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;
import static com.tinf.qmobile.model.ViewType.CLASS;
import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.PERIOD;

public class ClassAdapter extends RecyclerView.Adapter<ClassBaseViewHolder> {
    private List<Queryable> classes;
    private Context context;
    private DataSubscription sub1, sub2;

    public ClassAdapter(Context context, Bundle bundle) {
        this.context = context;

        classes = getList(bundle);

        DataObserver observer = data -> {
            Log.d("ClassAdapter", "Update detected");

            List<Queryable> updated = getList(bundle);

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {

                @Override
                public int getOldListSize() {
                    return classes.size();
                }

                @Override
                public int getNewListSize() {
                    return updated.size();
                }

                @Override
                public boolean areItemsTheSame(int o, int n) {
                    Queryable oldQ = classes.get(o);
                    Queryable newQ = updated.get(n);

                    if (oldQ instanceof Clazz && newQ instanceof Clazz)
                        return ((Clazz) oldQ).id == ((Clazz) newQ).id;

                    if (oldQ instanceof Period && newQ instanceof Period)
                        return ((Period) oldQ).id == ((Period) newQ).id;

                    else return oldQ instanceof Empty && newQ instanceof Empty;
                }

                @Override
                public boolean areContentsTheSame(int o, int n) {
                    return (classes.get(o).equals(updated.get(n)));
                }

            }, true);

            classes.clear();
            classes.addAll(updated);
            result.dispatchUpdatesTo(this);
        };

        sub1 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        sub2 = DataBase.get().getBoxStore().subscribe(Clazz.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);
    }

    private List<Queryable> getList(Bundle bundle) {
        ArrayList<Queryable> list = new ArrayList<>();

        Matter matter = DataBase.get().getBoxStore()
                .boxFor(Matter.class)
                .get(bundle.getLong("ID"));

        for (Period p : matter.periods)
            if (!p.classes.isEmpty()) {
                list.add(p);
                List<Clazz> cls = p.classes;
                Collections.reverse(cls);
                list.addAll(cls);
            }

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
        return classes.get(i).getItemType();
    }

    @Override
    public void onBindViewHolder(@NonNull ClassBaseViewHolder holder, int i) {
        holder.bind(context, classes.get(i));
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        sub1.cancel();
        sub2.cancel();
    }

}
