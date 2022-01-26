package com.tinf.qmobile.holder.search;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;
import androidx.annotation.NonNull;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.adapter.SearchAdapter;
import com.tinf.qmobile.databinding.SearchJournalBinding;
import com.tinf.qmobile.model.journal.Journal;

import static com.tinf.qmobile.model.ViewType.JOURNAL;

public class SearchJournalViewHolder extends SearchViewHolder<Journal> {
    private final SearchJournalBinding binding;

    public SearchJournalViewHolder(@NonNull View view) {
        super(view);
        binding = SearchJournalBinding.bind(view);
    }

    @Override
    public void bind(Journal journal, Context context, String query, SearchAdapter adapter) {
        binding.title.setText(journal.getTitle());
        binding.subtitle.setText(journal.getMatter());
        binding.date.setText(journal.formatDate());
        binding.icon.setImageTintList(ColorStateList.valueOf(journal.getColor()));

        //itemView.setOnClickListener(view -> onQuery.onQuery(journal.matter.getTargetId(), journal.id, GRADES));
        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("ID", journal.id);
            intent.putExtra("TYPE", JOURNAL);
            intent.putExtra("LOOKUP", true);
            context.startActivity(intent);

            saveQuery(query);
        });
    }

}
