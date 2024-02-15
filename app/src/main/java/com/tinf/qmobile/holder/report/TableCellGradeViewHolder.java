package com.tinf.qmobile.holder.report;

import static com.tinf.qmobile.utility.DesignUtils.getColorForGrade;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.color.ColorRoles;
import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.TableCellGradeBinding;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.utility.ColorsUtils;

public class TableCellGradeViewHolder extends TableBaseViewHolder {
  private final TableCellGradeBinding binding;

  public TableCellGradeViewHolder(
      @NonNull
      View view) {
    super(view);
    binding = TableCellGradeBinding.bind(view);
  }

  @Override
  public void bind(Context context,
                   Matter matter,
                   String cell) {
    binding.grade.setText(cell);

    if (cell.equals("-")) {
      binding.grade.setTextColor(
          ColorsUtils.getColor(context, com.google.android.material.R.attr.colorOnSurface));
      binding.color.setCardBackgroundColor(ColorsUtils.getColor(context, R.color.transparent));
    } else {
      ColorRoles colorRoles =
          getColorForGrade(context, Float.parseFloat(cell));

      binding.grade.setTextColor(colorRoles.getOnAccentContainer());
      binding.color.setCardBackgroundColor(colorRoles.getAccentContainer());
    }
  }

}
