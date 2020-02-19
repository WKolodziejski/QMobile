package com.tinf.qmobile.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.holder.journal.JournalBaseViewHolder;
import com.tinf.qmobile.holder.journal.JournalFooterViewHolder;
import com.tinf.qmobile.holder.journal.JournalHeaderViewHolder;
import com.tinf.qmobile.holder.journal.JournalViewHolder;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.Footer;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;
import java.util.ArrayList;
import java.util.List;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;
import static com.tinf.qmobile.model.Queryable.ViewType.FOOTER;
import static com.tinf.qmobile.model.Queryable.ViewType.HEADER;
import static com.tinf.qmobile.model.Queryable.ViewType.JOURNAL;
import static com.tinf.qmobile.network.Client.pos;

public class JournalAdapter extends RecyclerView.Adapter<JournalBaseViewHolder> implements OnUpdate {
    private List<Queryable> journals;
    private Context context;
    private OnExpandListener onExpandListener;
    private DataSubscription sub1, sub2;

    public JournalAdapter(Context context, OnExpandListener onExpandListener) {
        this.context = context;
        this.onExpandListener = onExpandListener;

        Client.get().addOnUpdateListener(this);

        BoxStore boxStore = DataBase.get().getBoxStore();

        journals = getList();

        DataObserver observer = data -> {
            List<Queryable> updated = new ArrayList<>(DataBase.get().getBoxStore()
                    .boxFor(Matter.class)
                    .query()
                    .order(Matter_.title_)
                    .equal(Matter_.year_, User.getYear(pos))
                    .and()
                    .equal(Matter_.period_, User.getPeriod(pos))
                    .build()
                    .find());

            for (int i = 0; i < journals.size(); i++) {
                if (journals.get(i) instanceof Matter) {
                    Matter m1 = (Matter) journals.get(i);
                    for (Queryable jb : updated) {
                        if (jb instanceof Matter) {
                            Matter m2 = (Matter) jb;
                            if (m1.id == m2.id) {
                                m2.isExpanded = m1.isExpanded;
                                m2.shouldAnimate = m1.shouldAnimate;
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < updated.size(); i++) {
                if (updated.get(i) instanceof Matter) {
                    Matter matter = (Matter) updated.get(i);

                    if (matter.isExpanded) {
                        List<Journal> items = matter.getLastPeriod().journals;
                        updated.addAll(i + 1, items);
                        updated.add(i + items.size() + 1, new Footer(i, matter));
                    }
                }
            }

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return journals.size();
                }

                @Override
                public int getNewListSize() {
                    return updated.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    if (journals.get(oldItemPosition) instanceof Matter && updated.get(newItemPosition) instanceof Matter)
                        return (((Matter) journals.get(oldItemPosition)).id == (((Matter) updated.get(newItemPosition)).id));

                    else if (journals.get(oldItemPosition) instanceof Journal && updated.get(newItemPosition) instanceof Journal)
                        return (((Journal) journals.get(oldItemPosition)).id == (((Journal) updated.get(newItemPosition)).id));

                    else if (journals.get(oldItemPosition) instanceof Footer && updated.get(newItemPosition) instanceof Footer)
                        return (((Footer) journals.get(oldItemPosition)).getMatter().id == (((Footer) updated.get(newItemPosition)).getMatter().id));

                    else return false;
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return (journals.get(oldItemPosition).equals(updated.get(newItemPosition)));
                }

            }, true);

            journals.clear();
            journals.addAll(updated);
            result.dispatchUpdatesTo(this);
        };

        sub1 = boxStore.subscribe(Matter.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        sub2 = boxStore.subscribe(Journal.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);
    }

    private List<Queryable> getList() {
        return new ArrayList<>(DataBase.get().getBoxStore()
                .boxFor(Matter.class)
                .query()
                .order(Matter_.title_)
                .equal(Matter_.year_, User.getYear(pos))
                .and()
                .equal(Matter_.period_, User.getPeriod(pos))
                .build()
                .find());
    }

    public void toggle() {
        int open = 0, closed = 0;

        for (Queryable jb : journals)
            if (jb instanceof Matter)
                if (((Matter) jb).isExpanded)
                    open++;
                else
                    closed++;

        if (closed > open) {
            for (int i = 0; i < journals.size(); i++)
                if (journals.get(i) instanceof Matter)
                    expand(i, (Matter) journals.get(i));

            Toast.makeText(context, R.string.diarios_expanded, Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < journals.size(); i++)
                if (journals.get(i) instanceof Matter)
                    collapse(i, (Matter) journals.get(i));

            Toast.makeText(context, R.string.diarios_collapsed, Toast.LENGTH_SHORT).show();
        }
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

            case FOOTER:
                return new JournalFooterViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.journal_footer, parent, false));

        }
        return null;
    }

    @Override
    public int getItemViewType(int i) {
        return journals.get(i).getItemType();
    }

    @Override
    public void onBindViewHolder(@NonNull JournalBaseViewHolder holder, int i) {
        holder.bind(context, journals.get(i), this);
    }

    public void expand(int i, Matter matter) {
        matter.isExpanded = true;

        notifyItemChanged(i);

        List<Journal> items = matter.getLastPeriod().journals;

        journals.addAll(i + 1, items);
        notifyItemRangeInserted(i + 1, items.size());

        journals.add(i + items.size() + 1, new Footer(i, matter));
        notifyItemInserted(i + items.size() + 1);

        onExpandListener.onExpand(i + items.size() + 1);
    }

    public void collapse(int i, Matter matter) {
        matter.isExpanded = false;

        notifyItemChanged(i);
        i++;
        while (i < journals.size()) {
            if (journals.get(i) instanceof Journal || journals.get(i) instanceof Footer) {
                journals.remove(i);
                notifyItemRemoved(i);
            } else break;
        }
    }

    @Override
    public int getItemCount() {
        return journals.size();
    }

    @Override
    public void onScrollRequest() {

    }

    @Override
    public void onDateChanged() {
        journals = getList();
        notifyDataSetChanged();
    }

    public interface OnExpandListener {
        void onExpand(int position);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        sub1.cancel();
        sub2.cancel();
    }

}
