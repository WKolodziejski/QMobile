package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.Journal;

import butterknife.BindView;

import static com.tinf.qmobile.activity.MatterActivity.GRADES;
import static com.tinf.qmobile.activity.MatterActivity.MATERIALS;
import static com.tinf.qmobile.model.ViewType.JOURNAL;

public class SearchJournalViewHolder extends SearchViewHolder<Journal> {
    @BindView(R.id.search_journal_title)        TextView title;
    @BindView(R.id.search_journal_subtitle)     TextView subtitle;
    @BindView(R.id.search_journal_date)         TextView date;
    @BindView(R.id.search_journal_icon)         ImageView icon;

    public SearchJournalViewHolder(@NonNull View view) {
        super(view);
    }

    @Override
    public void bind(Journal journal, Context context) {
        title.setText(journal.getTitle());
        subtitle.setText(journal.getMatter());
        date.setText(journal.formatDate());
        icon.setImageTintList(ColorStateList.valueOf(journal.getColor()));

        //itemView.setOnClickListener(view -> onQuery.onQuery(journal.matter.getTargetId(), journal.id, GRADES));
        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("ID", journal.id);
            intent.putExtra("TYPE", JOURNAL);
            context.startActivity(intent);
        });
    }

}
