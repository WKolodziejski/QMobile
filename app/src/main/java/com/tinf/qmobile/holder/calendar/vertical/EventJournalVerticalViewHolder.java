package com.tinf.qmobile.holder.calendar.vertical;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.journal.Journal;

import butterknife.BindView;

import static com.tinf.qmobile.model.ViewType.JOURNAL;

public class EventJournalVerticalViewHolder extends CalendarViewHolder<Journal> {
    @BindView(R.id.calendar_default_title)       TextView title;
    @BindView(R.id.calendar_default_description) TextView description;
    @BindView(R.id.calendar_default_card)        LinearLayout card;

    @BindView(R.id.calendar_header_simple_day_week)     TextView day;
    @BindView(R.id.calendar_header_simple_day_number)   TextView number;
    @BindView(R.id.calendar_header_simple_layout)       LinearLayout layout;

    public EventJournalVerticalViewHolder(View view) {
        super(view);
    }

    @Override
    public void bind(Journal journal, Context context) {
        title.setText(journal.getTitle().isEmpty() ? context.getString(R.string.event_no_title) : journal.getTitle());
        description.setText(journal.matter.getTarget().getTitle());
        card.setBackgroundColor(journal.getColor());

        card.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            intent.putExtra("TYPE", JOURNAL);
            intent.putExtra("ID", journal.id);
            context.startActivity(intent);
        });

        if (journal.isHeader) {
            day.setText(journal.getWeekString());
            number.setText(journal.getDayString());
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.INVISIBLE);
        }
    }

}
