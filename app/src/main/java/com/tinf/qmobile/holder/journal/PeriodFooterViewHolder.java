package com.tinf.qmobile.holder.journal;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.View;

import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.PeriodFooterBinding;
import com.tinf.qmobile.model.journal.FooterPeriod;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.utility.ColorUtils;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;

public class PeriodFooterViewHolder extends JournalBaseViewHolder<FooterPeriod> {
  private final PeriodFooterBinding binding;

  public PeriodFooterViewHolder(View view) {
    super(view);
    binding = PeriodFooterBinding.bind(view);
  }

  @Override
  public void bind(Context context, FooterPeriod footer, boolean lookup, boolean isHeader) {
    binding.partialGrade.setText(footer.period.getGradeSumString());
    binding.partialAverage.setText(footer.period.getPlotGradeString());
    binding.finalGrade.setText(footer.period.getGrade());
    binding.absences.setText(footer.period.getAbsences());
  }

}
