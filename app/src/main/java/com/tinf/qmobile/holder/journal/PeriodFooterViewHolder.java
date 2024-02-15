package com.tinf.qmobile.holder.journal;

import android.content.Context;
import android.view.View;

import com.tinf.qmobile.databinding.PeriodFooterBinding;
import com.tinf.qmobile.model.journal.FooterPeriod;

public class PeriodFooterViewHolder extends JournalBaseViewHolder<FooterPeriod> {
  private final PeriodFooterBinding binding;

  public PeriodFooterViewHolder(View view) {
    super(view);
    binding = PeriodFooterBinding.bind(view);
  }

  @Override
  public void bind(Context context, FooterPeriod footer, boolean lookup, boolean isHeader) {
    binding.partialGrade.setText(footer.period.getGradeSumString());
    binding.partialAverage.setText(footer.period.getPartialGradeString());
    binding.finalGrade.setText(footer.period.getGrade());
    binding.absences.setText(footer.period.getAbsences());
  }

}
