package com.tinf.qmobile.adapter;

import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.HEADER;
import static com.tinf.qmobile.network.Client.pos;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.holder.journal.JournalEmptyViewHolder;
import com.tinf.qmobile.holder.performance.EmptyViewHolder;
import com.tinf.qmobile.holder.performance.HeaderViewHolder;
import com.tinf.qmobile.holder.performance.PerformanceViewHolder;
import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.utility.User;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

public class PerformanceAdapter extends RecyclerView.Adapter<PerformanceViewHolder> {
    private final List<Queryable> matters;
    private final Context context;
    private final DataSubscription sub1;

    public PerformanceAdapter(Context context) {
        this.context = context;
        this.matters = getList();

        DataObserver observer = data -> {
            List<Queryable> updated = getList();

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {

                @Override
                public int getOldListSize() {
                    return matters.size();
                }

                @Override
                public int getNewListSize() {
                    return updated.size();
                }

                @Override
                public boolean areItemsTheSame(int o, int n) {
                    Queryable oldQ = matters.get(o);
                    Queryable newQ = updated.get(n);

                    if (oldQ instanceof Matter && newQ instanceof Matter)
                        return ((Matter) oldQ).id == ((Matter) newQ).id;

                    else return oldQ instanceof Empty && newQ instanceof Empty;
                }

                @Override
                public boolean areContentsTheSame(int o, int n) {
                    return matters.get(o).equals(updated.get(n));
                }

            }, true);

            matters.clear();
            matters.addAll(updated);
            result.dispatchUpdatesTo(this);
        };

        sub1 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e("Adapter", th.toString()))
                .observer(observer);
    }

    private List<Queryable> getList() {
        List<Matter> matters = DataBase.get().getBoxStore()
                .boxFor(Matter.class)
                .query()
                .order(Matter_.title_)
                .equal(Matter_.year_, User.getYear(pos))
                .and()
                .equal(Matter_.period_, User.getPeriod(pos))
                .build()
                .find();

        List<Queryable> list = new ArrayList<>();

        for (Matter matter : matters) {
            int i = 0;

            if (!matter.periods.isEmpty())
                for (Period period : matter.periods)
                    if (!period.journals.isEmpty())
                        for (Journal journal : period.journals)
                            if (journal.getGrade_() >= 0)
                                i++;

            Log.d(matter.getTitle(), String.valueOf(i));

            if (i > 1)
                list.add(matter);
        }

        if (list.isEmpty())
            list.add(new Empty());

        return list;
    }

    @Override
    public int getItemViewType(int i) {
        return matters.get(i).getItemType();
    }

    @NonNull
    @Override
    public PerformanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                return new HeaderViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.chart_header, parent, false));

            case EMPTY:
                return new EmptyViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.chart_empty, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PerformanceViewHolder holder, int i) {
        holder.bind(context, matters.get(i));
    }

    @Override
    public int getItemCount() {
        return matters.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        sub1.cancel();
    }

}
