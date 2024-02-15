package com.tinf.qmobile.holder.journal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.color.ColorRoles;
import com.tinf.qmobile.databinding.PeriodHeaderBinding;
import com.tinf.qmobile.model.matter.Period;
import com.tinf.qmobile.utility.ColorsUtils;

public class PeriodHeaderViewHolder extends JournalBaseViewHolder<Period> {
  private final PeriodHeaderBinding binding;

  public PeriodHeaderViewHolder(
      @NonNull
      View view) {
    super(view);
    binding = PeriodHeaderBinding.bind(view);
  }

  @Override
  public void bind(Context context,
                   Period header,
                   boolean lookup,
                   boolean isHeader) {
    ColorRoles colorRoles = ColorsUtils.harmonizeWithPrimary(context, header.matter.getTarget()
                                                                                   .getColor());

    binding.title.setText(header.getTitle());
    binding.badge.setBackgroundTintList(ColorStateList.valueOf(colorRoles.getAccentContainer()));
  }

}
