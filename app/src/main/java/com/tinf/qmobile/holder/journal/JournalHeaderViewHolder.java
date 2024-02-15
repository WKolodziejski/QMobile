package com.tinf.qmobile.holder.journal;

import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MATTER;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;

import com.google.android.material.color.ColorRoles;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.databinding.JournalHeaderBinding;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.utility.ColorsUtils;

public class JournalHeaderViewHolder extends JournalBaseViewHolder<Matter> {
  private final JournalHeaderBinding binding;

  public JournalHeaderViewHolder(View view) {
    super(view);
    binding = JournalHeaderBinding.bind(view);
  }

  @Override
  public void bind(Context context, Matter matter, boolean lookup, boolean isHeader) {
    ColorRoles colorRoles = ColorsUtils.harmonizeWithPrimary(context, matter.getColor());

    binding.title.setText(matter.getTitle());
    binding.badge.setVisibility(isHeader ? View.INVISIBLE : View.VISIBLE);
    binding.badge.setBackgroundTintList(ColorStateList.valueOf(colorRoles.getAccentContainer()));
    binding.badge.setTextColor(colorRoles.getOnAccentContainer());
    binding.header.setVisibility(matter.hasJournals() ? View.VISIBLE : View.GONE);
    itemView.setOnClickListener(view -> {
      Intent intent = new Intent(context, MatterActivity.class);
      intent.putExtra("ID", matter.id);
      intent.putExtra("PAGE", MATTER);
      intent.putExtra("LOOKUP", lookup);
      context.startActivity(intent);
    });

    int n = matter.getJournalNotSeenCount();
    binding.badge.setText(n > 0 ? String.valueOf(n) : "");
  }

}
