package com.tinf.qmobile.holder.calendar.vertical;

import static com.tinf.qmobile.model.ViewType.JOURNAL;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.databinding.CalendarEventJournalVBinding;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.model.journal.Journal;

public class EventJournalVerticalViewHolder extends CalendarViewHolder<Journal> {
  private final CalendarEventJournalVBinding binding;

  public EventJournalVerticalViewHolder(View view) {
    super(view);
    binding = CalendarEventJournalVBinding.bind(view);
  }

  @Override
  public void bind(Journal journal, Context context) {
    binding.title.setText(journal.getTitle().isEmpty() ? context.getString(R.string.event_no_title)
                                                       : journal.getTitle());
    //description.setText(journal.matter.getTarget().getTitle());
    binding.card.setBackgroundColor(journal.getColor());

    binding.card.setOnClickListener(v -> {
      Intent intent = new Intent(context, EventViewActivity.class);
      intent.putExtra("TYPE", JOURNAL);
      intent.putExtra("ID", journal.id);
      intent.putExtra("LOOKUP", true);
      context.startActivity(intent);
    });

    if (journal.isHeader) {
      binding.header.day.setText(journal.getWeekString());
      binding.header.number.setText(journal.getDayString());
      binding.header.layout.setVisibility(View.VISIBLE);
    } else {
      binding.header.layout.setVisibility(View.INVISIBLE);
    }
  }

}
