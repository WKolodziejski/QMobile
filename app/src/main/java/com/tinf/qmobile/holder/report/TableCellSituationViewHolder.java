package com.tinf.qmobile.holder.report;

import android.content.Context;
import android.view.View;

import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.TableCellSituationBinding;
import com.tinf.qmobile.model.matter.Matter;

public class TableCellSituationViewHolder extends TableBaseViewHolder {
  private final TableCellSituationBinding binding;

  public TableCellSituationViewHolder(View view) {
    super(view);
    binding = TableCellSituationBinding.bind(view);
  }

  @Override
  public void bind(Context context, Matter matter, String cell) {
    binding.situation.setText(cell);

    if (cell.equals("-")) {
      binding.situation.setTextColor(context.getColor(R.color.colorPrimary));
      binding.color.setCardBackgroundColor(context.getColor(R.color.transparent));
    } else {
      binding.situation.setTextColor(context.getColor(R.color.white));
      binding.color.setCardBackgroundColor(getSituationColor(context, cell));
    }
  }

  private static int getSituationColor(Context context, String s) {
    if (s.contains("Aprovado"))
      return context.getColor(R.color.good);

    if (s.contains("Reprovado"))
      return context.getColor(R.color.bad);

    if (s.contains("Falta"))
      return context.getColor(R.color.bad);

    if (s.contains("Cursando"))
      return context.getColor(R.color.ok);

    return context.getColor(R.color.colorPrimary);
  }

}