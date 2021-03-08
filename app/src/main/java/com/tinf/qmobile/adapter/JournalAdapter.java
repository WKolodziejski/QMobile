package com.tinf.qmobile.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kodmap.library.kmrecyclerviewstickyheader.KmStickyListener;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.holder.journal.JournalBaseViewHolder;
import com.tinf.qmobile.holder.journal.JournalEmptyViewHolder;
import com.tinf.qmobile.holder.journal.JournalFooterViewHolder;
import com.tinf.qmobile.holder.journal.JournalHeaderViewHolder;
import com.tinf.qmobile.holder.journal.JournalViewHolder;
import com.tinf.qmobile.holder.journal.PeriodFooterViewHolder;
import com.tinf.qmobile.holder.journal.PeriodHeaderViewHolder;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.calendar.Header;
import com.tinf.qmobile.model.journal.FooterJournal;
import com.tinf.qmobile.model.journal.FooterPeriod;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;
import java.util.ArrayList;
import java.util.List;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;
import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.FOOTERJ;
import static com.tinf.qmobile.model.ViewType.FOOTERP;
import static com.tinf.qmobile.model.ViewType.HEADER;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.PERIOD;
import static com.tinf.qmobile.network.Client.pos;

public class JournalAdapter extends RecyclerView.Adapter<JournalBaseViewHolder> implements OnUpdate,
        KmStickyListener {
    private List<Queryable> journals;
    private Context context;
    private OnExpandListener onExpandListener;
    private DataSubscription sub1, sub2;

    public JournalAdapter(Context context, Bundle bundle, OnExpandListener onExpandListener) {
        this.context = context;
        this.onExpandListener = onExpandListener;

        Client.get().addOnUpdateListener(this);

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
            } else {
                for (int i = 0; i < journals.size(); i++) {
                    if (journals.get(i) instanceof Journal) {
                        Journal j1 = ((Journal) journals.get(i));

                        for (Queryable q : updated)
                            if (q instanceof Journal) {
                                Journal j2 = (Journal) q;

                                if (j1.id == j2.id) {
                                    j2.highlight = j1.highlight;
                                    break;
                                }
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
                    Queryable oldQ = journals.get(o);
                    Queryable newQ = updated.get(n);

                    if (oldQ instanceof Matter && newQ instanceof Matter)
                        return (((Matter) oldQ).id == (((Matter) newQ).id));

                    else if (oldQ instanceof Journal && newQ instanceof Journal)
                        return (((Journal) oldQ).id == (((Journal) newQ).id));

                    else if (oldQ instanceof FooterJournal && newQ instanceof FooterJournal)
                        return (((FooterJournal) oldQ).getMatter().id == (((FooterJournal) newQ).getMatter().id));

                    else if (oldQ instanceof FooterPeriod && newQ instanceof FooterPeriod)
                        return (((FooterPeriod) oldQ).period.id == (((FooterPeriod) newQ).period.id));

                    else if (oldQ instanceof Period && newQ instanceof Period)
                        return (((Period) oldQ).id == (((Period) newQ).id));

                    else return oldQ instanceof Empty && newQ instanceof Empty;
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

        sub1 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        sub2 = DataBase.get().getBoxStore().subscribe(Journal.class)
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
                    .get(bundle.getLong("ID"));

            for (Period period : matter.periods) {
                if (!period.journals.isEmpty()) {
                    list.add(period);
                    list.addAll(period.journals);

                    if (!period.isSub_())
                        list.add(new FooterPeriod(period));
                }
            }
        }

        if (list.isEmpty())
            list.add(new Empty());

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

            case EMPTY:
                return new JournalEmptyViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.journal_empty, parent, false));

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

    public int highlight(long id) {
        for (int i = 0; i < journals.size(); i++) {
            Queryable q = journals.get(i);

            if (q instanceof Journal) {
                Journal j = (Journal) q;

                if (j.id == id) {
                    j.highlight = true;
                    notifyItemChanged(i);
                    return i;
                }
            }
        }

        return -1;
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

    @Override
    public Integer getHeaderPositionForItem(Integer i) {
        Queryable q = journals.get(i);

        if (q instanceof Journal)
            while (!(q instanceof Matter) && i > 0)
                q = journals.get(--i);

        return i;
    }

    @Override
    public Integer getHeaderLayout(Integer i) {
        if (journals.get(i) instanceof Matter && ((Matter) journals.get(i)).isExpanded)
            return R.layout.journal_header;
        else
            return R.layout.header_empty;
    }

    @Override
    public void bindHeaderData(View header, Integer i) {
        if (journals.get(i) instanceof Matter) {
            //TODO
        }
    }

    @Override
    public Boolean isHeader(Integer i) {
        Queryable q = journals.get(i);

        if (i >= 0 && i < journals.size())
            return !(q instanceof Journal);
        else return false;
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
