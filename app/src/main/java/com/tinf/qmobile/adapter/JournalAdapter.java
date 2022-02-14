package com.tinf.qmobile.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
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
import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.FooterJournal;
import com.tinf.qmobile.model.journal.FooterPeriod;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.Design;
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
import static com.tinf.qmobile.model.ViewType.JOURNALEMPTY;
import static com.tinf.qmobile.model.ViewType.PERIOD;
import static com.tinf.qmobile.network.Client.pos;

public class JournalAdapter extends RecyclerView.Adapter<JournalBaseViewHolder> implements OnUpdate, KmStickyListener {
    //private List<Queryable> journals;
    private final Context context;
    private final AsyncListDiffer<Queryable> journals;
    private final DataSubscription sub1;
    private final DataSubscription sub2;
    private final Design.OnDesign onDesign;
    private final boolean lookup;

    public JournalAdapter(Context context, Bundle bundle, Design.OnDesign onDesign) {
        this.context = context;
        this.onDesign = onDesign;
        this.lookup = bundle == null;
        this.journals = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<Queryable>() {
            @Override
            public boolean areItemsTheSame(@NonNull Queryable oldItem, @NonNull Queryable newItem) {
                return oldItem.getId() == newItem.getId() && oldItem.getItemType() == newItem.getItemType();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Queryable oldItem, @NonNull Queryable newItem) {
                return oldItem.isSame(newItem);
            }
        });

        Client.get().addOnUpdateListener(this);

        /*DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {

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
                Queryable oldQ = journals.getCurrentList().get(o);
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
                return journals.getCurrentList().get(o).equals(updated.get(n));
            }

        }, true);*/

        journals.submitList(getList(bundle));

        DataObserver observer = data -> {

            List<Queryable> updated = getList(bundle);

            /*if (bundle != null) {
                for (int i = 0; i < journals.getCurrentList().size(); i++) {
                    if (journals.getCurrentList().get(i) instanceof Journal) {
                        Journal j1 = ((Journal) journals.getCurrentList().get(i));

                        for (Queryable q : updated) {
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
            }*/

            //journals.clear();
            //journals.addAll(updated);
            //result.dispatchUpdatesTo(this);
            journals.submitList(updated);
        };

        sub1 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(Throwable::printStackTrace)
                .observer(observer);

        sub2 = DataBase.get().getBoxStore().subscribe(Journal.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(Throwable::printStackTrace)
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

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof Matter) {
                    Matter matter = (Matter) list.get(i);

                    List<Journal> items = matter.getLastJournals();

                    if (items.isEmpty()) {
                        list.add(i + 1, new Empty(JOURNALEMPTY));
                        list.add(i + 2, new FooterJournal(i, matter));
                    } else {
                        list.addAll(i + 1, items);
                        list.add(i + items.size() + 1, new FooterJournal(i, matter));
                    }
                }
            }

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

        if (list.isEmpty()) {
            onDesign.onToolbar(false);
            list.add(new Empty());
        } else {
            onDesign.onToolbar(true);
        }

        return list;
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

            case JOURNALEMPTY:
                return new JournalEmptyViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.journal_item_empty, parent, false));

            case EMPTY:
                return new JournalEmptyViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.journal_empty, parent, false));
        }

        return null;
    }

    @Override
    public int getItemViewType(int i) {
        return journals.getCurrentList().get(i).getItemType();
    }

    @Override
    public void onBindViewHolder(@NonNull JournalBaseViewHolder holder, int i) {
        holder.bind(context, journals.getCurrentList().get(i), this, lookup);
    }

    /*public int highlight(long id) {
        for (int i = 0; i < journals.getCurrentList().size(); i++) {
            Queryable q = journals.getCurrentList().get(i);

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
    }*/

    @Override
    public int getItemCount() {
        return journals.getCurrentList().size();
    }

    @Override
    public void onScrollRequest() {

    }

    @Override
    public void onDateChanged() {
        //journals = getList(null);
        //notifyDataSetChanged();
        journals.submitList(getList(null));

        Log.d("JournalAdapter", "onDateChanged");
    }

    @Override
    public Integer getHeaderPositionForItem(Integer i) {
        Queryable q = journals.getCurrentList().get(i);

        if (q instanceof Journal)
            while (!(q instanceof Matter) && i > 0)
                q = journals.getCurrentList().get(--i);

        return i;
    }

    @Override
    public Integer getHeaderLayout(Integer i) {
        if (journals.getCurrentList().get(i) instanceof Matter) //&& ((Matter) journals.getCurrentList().get(i)).isExpanded)
            return R.layout.journal_header;
        else
            return R.layout.header_empty;
    }

    @Override
    public void bindHeaderData(View header, Integer i) {
        if (journals.getCurrentList().get(i) instanceof Matter) {// && ((Matter) journals.getCurrentList().get(i)).isExpanded) {
            Matter matter = (Matter) journals.getCurrentList().get(i);

            TextView title = header.findViewById(R.id.title);
            TextView badge = header.findViewById(R.id.badge);

            title.setText(matter.getTitle());
            badge.setBackgroundTintList(ColorStateList.valueOf(matter.getColor()));

            int n = matter.getJournalNotSeenCount();

            if (n > 0) {
                badge.setText(String.valueOf(n));
            } else {
                badge.setText("");
            }
        }
    }

    @Override
    public Boolean isHeader(Integer i) {
        Queryable q = journals.getCurrentList().get(i);

        if (i >= 0)
            return !(q instanceof Journal);
        else return false;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        sub1.cancel();
        sub2.cancel();

        Log.d("JournalAdapter", "onDetachedFromRecyclerView");
    }

}
