package com.tinf.qmobile.holder.calendar;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.model.calendar.base.CalendarBase;
import com.tinf.qmobile.model.journal.Journal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventJournalViewHolder extends CalendarViewHolder<Journal> {
    @BindView(R.id.calendar_default_title)       public TextView title;
    @BindView(R.id.calendar_default_description) public TextView description;
    @BindView(R.id.calendar_default_card)        public LinearLayout card;

    public EventJournalViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Journal journal, Context context) {
        title.setText(journal.getTitle().isEmpty() ? context.getString(R.string.event_no_title) : journal.getTitle());
        description.setText(journal.matter.getTarget().getTitle());
        card.setBackgroundColor(journal.getColor());

        card.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("TYPE", CalendarBase.ViewType.JOURNAL);
            intent.putExtra("ID", journal.id);
            context.startActivity(intent);
        });
    }

}
