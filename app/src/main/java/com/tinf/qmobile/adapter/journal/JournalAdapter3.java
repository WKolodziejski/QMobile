package com.tinf.qmobile.adapter.journal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.holder.Journal.JournalBaseViewHolder;
import com.tinf.qmobile.holder.Journal.JournalFooterViewHolder;
import com.tinf.qmobile.holder.Journal.JournalHeaderViewHolder;
import com.tinf.qmobile.holder.Journal.JournalViewHolder;
import com.tinf.qmobile.model.journal.Footer;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.journal.JournalBase;
import com.tinf.qmobile.model.matter.Matter;

import java.util.List;

public class JournalAdapter3 extends RecyclerView.Adapter<JournalBaseViewHolder> {
    private List<JournalBase> journals;
    private Context context;
    private View.OnClickListener listener;

    public JournalAdapter3(Context context, List<JournalBase> journals, View.OnClickListener listener) {
        this.journals = journals;
        this.context = context;
        this.listener = listener;
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
