package com.tinf.qmobile.holder.calendar.horizontal;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.journal.Journal;

import butterknife.BindView;

import static com.tinf.qmobile.model.ViewType.JOURNAL;

public class EventJournalHorizontalViewHolder extends CalendarViewHolder<Journal> {
    @BindView(R.id.calendar_default_title)       public TextView title;
    @BindView(R.id.calendar_default_description) public TextView description;
    @BindView(R.id.calendar_default_date)        public TextView date;
    @BindView(R.id.calendar_default_card)        public ConstraintLayout card;

    public EventJournalHorizontalViewHolder(View view) {
        super(view);
    }

    @Override
    public void bind(Journal journal, Context context) {
        title.setText(journal.getTitle().isEmpty() ? context.getString(R.string.event_no_title) : journal.getTitle());
        date.setText(journal.getStartDateString());
        description.setText(journal.matter.getTarget().getTitle());
        card.setBackgroundColor(journal.getColor());

        card.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("TYPE", JOURNAL);
            intent.putExtra("ID", journal.id);
            context.startActivity(intent);
        });
    }

}
