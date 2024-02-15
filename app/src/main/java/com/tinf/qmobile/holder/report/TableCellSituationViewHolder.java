package com.tinf.qmobile.holder.report;

import static com.tinf.qmobile.utility.DesignUtils.getColorForSituation;

import android.content.Context;
import android.view.View;

import com.google.android.material.color.ColorRoles;
import com.tinf.qmobile.databinding.TableCellSituationBinding;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.utility.ColorsUtils;

public class TableCellSituationViewHolder extends TableBaseViewHolder {
  private final TableCellSituationBinding binding;

  public TableCellSituationViewHolder(View view) {
    super(view);
    binding = TableCellSituationBinding.bind(view);
  }

  @Override
  public void bind(Context context,
                   Matter matter,
                   String cell) {
    binding.situation.setText(cell);

    if (cell.equals("-")) {
      ColorRoles colorRoles =
          ColorsUtils.getColorRoles(context, com.google.android.material.R.attr.colorSurface);

      binding.situation.setTextColor(colorRoles.getOnAccent());
      binding.color.setCardBackgroundColor(colorRoles.getAccent());
    } else {
      ColorRoles colorRoles = getColorForSituation(context, cell);

      binding.situation.setTextColor(colorRoles.getOnAccentContainer());
      binding.color.setCardBackgroundColor(colorRoles.getAccentContainer());
    }
  }

}