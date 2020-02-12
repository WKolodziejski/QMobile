package com.tinf.qmobile.adapter.journal;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.holder.Journal.JournalBaseViewHolder;
import com.tinf.qmobile.holder.Journal.JournalFooterViewHolder;
import com.tinf.qmobile.holder.Journal.JournalHeaderViewHolder;
import com.tinf.qmobile.holder.Journal.JournalViewHolder;
import com.tinf.qmobile.model.journal.Footer;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.journal.JournalBase;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.utility.User;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;

import static com.tinf.qmobile.network.Client.pos;

public class JournalAdapter3 extends RecyclerView.Adapter<JournalBaseViewHolder> {
    private List<JournalBase> journals;
    private Context context;

    public JournalAdapter3(Context context) {
        this.context = context;

        BoxStore boxStore = DataBase.get().getBoxStore();

        journals = new ArrayList<>(boxStore
                .boxFor(Matter.class)
                .query()
                .order(Matter_.title_)
                .equal(Matter_.year_, User.getYear(pos))
                .and()
                .equal(Matter_.period_, User.getPeriod(pos))
                .build()
                .find());

        boxStore.subscribe(Matter.class)
                .on(AndroidScheduler.mainThread())
                .onError(th -> {
                    Log.e(th.getMessage(), th.toString());
                })
                .observer(data -> {
                    List<JournalBase> updated = new ArrayList<>(boxStore
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
                            for (JournalBase jb : updated) {
                                if (jb instanceof Matter) {
                                    Matter m2 = (Matter) jb;

                                    if (m1.id == m2.id) {
                                        m2.isExpanded = m1.isExpanded;
                                        m2.shouldAnimate = m1.shouldAnimate;
                                        journals.remove(i);
                                        journals.add(i, m2);
                                        notifyItemChanged(i);

                                        int j = i;
                                        while (journals.get(j).getItemType() != JournalBase.ViewType.FOOTER) {
                                            j++;
                                        }

                                        journals.remove(j);
                                        journals.add(j, new Footer(i, m2));

                                        notifyItemChanged(j);
                                    }
                                }
                            }
                        }
                    }
                });
    }

    @NonNull
    @Override
    public JournalBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case JournalBase.ViewType.HEADER:
                return new JournalHeaderViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.list_journal_header, parent, false));

            case JournalBase.ViewType.JOURNAL:
                return new JournalViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.list_journal, parent, false));

            case JournalBase.ViewType.FOOTER:
                return new JournalFooterViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.list_journal_footer, parent, false));

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
        notifyItemChanged(i);

        List<Journal> items = matter.getLastPeriod().journals;

        for (int j = 0; j < items.size(); j++) {
            journals.add(j + i + 1, items.get(j));
            notifyItemInserted(j + i + 1);
        }

        journals.add(i + items.size() + 1, new Footer(i, matter));
        notifyItemInserted(i + items.size() + 1);
    }

    public void collapse(int i) {
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

}
