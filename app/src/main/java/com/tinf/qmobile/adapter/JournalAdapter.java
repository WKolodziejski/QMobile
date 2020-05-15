package com.tinf.qmobile.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.holder.journal.JournalBaseViewHolder;
import com.tinf.qmobile.holder.journal.JournalFooterViewHolder;
import com.tinf.qmobile.holder.journal.JournalHeaderViewHolder;
import com.tinf.qmobile.holder.journal.JournalViewHolder;
import com.tinf.qmobile.holder.journal.PeriodFooterViewHolder;
import com.tinf.qmobile.holder.journal.PeriodHeaderViewHolder;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.FooterJournal;
import com.tinf.qmobile.model.journal.FooterPeriod;
import com.tinf.qmobile.model.journal.Header;
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
import static com.tinf.qmobile.model.Queryable.ViewType.FOOTERP;
import static com.tinf.qmobile.model.Queryable.ViewType.FOOTERJ;
import static com.tinf.qmobile.model.Queryable.ViewType.HEADER;
import static com.tinf.qmobile.model.Queryable.ViewType.JOURNAL;
import static com.tinf.qmobile.model.Queryable.ViewType.PERIOD;
import static com.tinf.qmobile.network.Client.pos;

public class JournalAdapter extends RecyclerView.Adapter<JournalBaseViewHolder> implements OnUpdate {
    private List<Queryable> journals;
    private Context context;
    private OnExpandListener onExpandListener;
    private DataSubscription sub1, sub2;

    public JournalAdapter(Context context, Bundle bundle, OnExpandListener onExpandListener) {
        this.context = context;
        this.onExpandListener = onExpandListener;

        Client.get().addOnUpdateListener(this);

        BoxStore boxStore = DataBase.get().getBoxStore();

        journals = getList(bundle);

        DataObserver observer = data -> {

            List<Queryable> updated = getList(bundle);

            if (bundle == null) {
                for (int i = 0; i < journals.size(); i++) {
                    if (journals.get(i) instanceof Matter) {
                        Matter m1 = (Matter) journals.get(i);
                        for (Queryable jb : updated) {
                            if (jb instanceof Matter) {
                                Matter m2 = (Matter) jb;
                                if (m1.id == m2.id) {
                                    m2.isExpanded = m1.isExpanded;
                                    m2.shouldAnimate = m1.shouldAnimate;
                                    break;
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
                            updated.add(i + items.size() + 1, new FooterJournal(i, matter));
                        }
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
                public boolean areItemsTheSame(int o, int n) {
                    if (journals.get(o) instanceof Matter && updated.get(n) instanceof Matter)
                        return (((Matter) journals.get(o)).id == (((Matter) updated.get(n)).id));

                    else if (journals.get(o) instanceof Journal && updated.get(n) instanceof Journal)
                        return (((Journal) journals.get(o)).id == (((Journal) updated.get(n)).id));

                    else if (journals.get(o) instanceof FooterJournal && updated.get(n) instanceof FooterJournal)
                        return (((FooterJournal) journals.get(o)).getMatter().id == (((FooterJournal) updated.get(n)).getMatter().id));

                    else if (journals.get(o) instanceof FooterPeriod && updated.get(n) instanceof FooterPeriod)
                        return (((FooterPeriod) journals.get(o)).getPeriod().id == (((FooterPeriod) updated.get(n)).getPeriod().id));

                    else if (journals.get(o) instanceof Header && updated.get(n) instanceof Header)
                        return (((Header) journals.get(o)).getPeriod().id == (((Header) updated.get(n)).getPeriod().id));

                    else return false;
                }

                @Override
                public boolean areContentsTheSame(int o, int n) {
                    return (journals.get(o).equals(updated.get(n)));
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

    private List<Queryable> getList(Bundle bundle) {
        ArrayList<Queryable> list = new ArrayList<>();

        if (bundle == null) {
            list.addAll(DataBase.get().getBoxStore()
                    .boxFor(Matter.class)
                    .query()
                    .order(Matter_.title_)
                    .equal(Matter_.year_, User.getYear(pos))
                    .and()
                    .equal(Matter_.period_, User.getPeriod(pos))
                    .build()
                    .find());
        } else {
            Matter matter = DataBase.get().getBoxStore()
                    .boxFor(Matter.class)
                    .query()
                    .equal(Matter_.id, bundle.getLong("ID"))
                    .build()
                    .findUnique();

            for (int i = 0; i < matter.periods.size(); i++) {
                if (!matter.periods.get(i).journals.isEmpty()) {
                    list.add(new Header(matter.periods.get(i)));
                    list.addAll(matter.periods.get(i).journals);
                    if (!matter.periods.get(i).isSub_())
                        list.add(new FooterPeriod(matter.periods.get(i)));
                }
            }
        }

        return list;
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
                    if (!((Matter) journals.get(i)).isExpanded)
                        expand(i, (Matter) journals.get(i), false);

            Toast.makeText(context, R.string.diarios_expanded, Toast.LENGTH_SHORT).show();

        } else {
            for (int i = 0; i < journals.size(); i++)
                if (journals.get(i) instanceof Matter)
                    if (((Matter) journals.get(i)).isExpanded)
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

            case FOOTERJ:
                return new JournalFooterViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.journal_footer, parent, false));

            case FOOTERP:
                return new PeriodFooterViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.period_footer, parent, false));

            case PERIOD:
                return new PeriodHeaderViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.period_header, parent, false));

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

    public void expand(int i, Matter matter, boolean scroll) {
        matter.isExpanded = true;

        notifyItemChanged(i);

        List<Journal> items = matter.getLastPeriod().journals;

        journals.addAll(i + 1, items);
        notifyItemRangeInserted(i + 1, items.size());

        journals.add(i + items.size() + 1, new FooterJournal(i, matter));
        notifyItemInserted(i + items.size() + 1);

        if (scroll)
            onExpandListener.onExpand(i + items.size() + 1);
    }

    public void collapse(int i, Matter matter) {
        matter.isExpanded = false;

        notifyItemChanged(i);
        i++;
        while (i < journals.size()) {
            if (journals.get(i) instanceof Journal || journals.get(i) instanceof FooterJournal) {
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
        journals = getList(null);
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
