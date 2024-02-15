package com.tinf.qmobile.holder.report;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;

import com.google.android.material.color.ColorRoles;
import com.tinf.qmobile.databinding.TableRowBinding;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.utility.ColorsUtils;

public class TableRowHeaderViewHolder extends TableBaseViewHolder {
  private final TableRowBinding binding;

  public TableRowHeaderViewHolder(View view) {
    super(view);
    binding = TableRowBinding.bind(view);
  }

  @Override
  public void bind(Context context,
                   Matter matter,
                   String cell) {
    ColorRoles colorRoles =
        ColorsUtils.harmonizeWithPrimary(context, matter.getColor());

    int n = matter.getJournalNotSeenCount();
    binding.badge.setBackgroundTintList(ColorStateList.valueOf(colorRoles.getAccentContainer()));
    binding.badge.setTextColor(colorRoles.getOnAccentContainer());
    binding.badge.setText(n > 0 ? String.valueOf(n) : "");
  }

}
