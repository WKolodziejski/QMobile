package com.tinf.qmobile.holder.journal;

import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.utility.DesignUtils.getColorForGrade;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import com.google.android.material.color.ColorRoles;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.databinding.JournalItemBinding;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.utility.ColorsUtils;

public class JournalItemViewHolder extends JournalBaseViewHolder<Journal> {
  private final JournalItemBinding binding;

  public JournalItemViewHolder(View view) {
    super(view);
    binding = JournalItemBinding.bind(view);
  }

  @Override
  public void bind(Context context,
                   Journal journal,
                   boolean lookup,
                   boolean isHeader) {
    binding.title.setText(journal.getTitle());
    binding.weight.setText(journal.getWeight());
    binding.date.setText(journal.formatDate());
    binding.max.setText(journal.getMax());
    binding.grade.setText(journal.getGrade());

    itemView.setOnClickListener(view -> {
      Intent intent = new Intent(context, EventViewActivity.class);
      intent.putExtra("ID", journal.id);
      intent.putExtra("TYPE", JOURNAL);
      intent.putExtra("LOOKUP", lookup);
      context.startActivity(intent);
      itemView.setBackgroundColor(context.getColor(R.color.transparent));
    });

    if (journal.isSeen_()) {
      itemView.setBackgroundColor(
          ColorsUtils.getColor(context, R.color.transparent));
      setTextColor(ColorsUtils.getColor(context,
                                        com.google.android.material.R.attr.colorOnSurface));
    } else {
      itemView.setBackgroundColor(
          ColorsUtils.getColor(context, com.google.android.material.R.attr.colorPrimaryContainer));
      setTextColor(ColorsUtils.getColor(context,
                                        com.google.android.material.R.attr.colorOnPrimaryContainer));
      journal.see();
      DataBase.get()
              .getBoxStore()
              .boxFor(Journal.class)
              .put(journal);
    }

    if (journal.getGrade()
               .equals("-")) {
      binding.grade.setTextColor(ColorsUtils.getColor(context, com.google.android.material.R.attr.colorOnSurface));
      binding.color.setCardBackgroundColor(ColorsUtils.getColor(context, R.color.transparent));
    } else {
      ColorRoles colorRoles =
          getColorForGrade(context, journal.getGrade_() / journal.getMax_() * 10);

      binding.grade.setTextColor(colorRoles.getOnAccentContainer());
      binding.color.setCardBackgroundColor(colorRoles.getAccentContainer());
    }
  }

  private void setTextColor(int color) {
    binding.title.setTextColor(color);
    binding.weight.setTextColor(color);
    binding.date.setTextColor(color);
    binding.max.setTextColor(color);
    binding.grade.setTextColor(color);
  }

}
