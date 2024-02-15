package com.tinf.qmobile.holder.report;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.color.ColorRoles;
import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.TableCellAbsencesBinding;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.utility.ColorUtils;
import com.tinf.qmobile.utility.ColorsUtils;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;

public class TableCellAbsencesViewHolder extends TableBaseViewHolder {
  private final TableCellAbsencesBinding binding;

  public TableCellAbsencesViewHolder(
      @NonNull
      View view) {
    super(view);
    binding = TableCellAbsencesBinding.bind(view);
  }

  @Override
  public void bind(Context context, Matter matter, String cell) {
    ColorRoles colorRoles = ColorsUtils.harmonizeWithPrimary(context, matter.getColor());

    int classesGiven = matter.getClassesGiven();
    int classesTotal = matter.getClassesTotal();
    int classesLeft = classesTotal - classesGiven;
    int absences = matter.getAbsences();
    int presences = Math.max(0, classesGiven - absences);

    if (classesGiven == 0 && absences > 0) {
      classesLeft -= absences;
    }

    binding.absences.setText(matter.getAbsencesString());
    binding.chartPresence.setVisibility(matter.getAbsences_() >= 0 ? VISIBLE : INVISIBLE);

    List<SliceValue> values = new ArrayList<>();

    values.add(new SliceValue(classesLeft)
                   .setColor(context.getColor(R.color.colorPrimaryDark))
                   .setLabel(""));

    if (presences > 0) {
      values.add(new SliceValue(presences)
                     .setColor(colorRoles.getAccentContainer())
                     .setLabel(""));
    }

    if (absences > 0) {
      values.add(new SliceValue(absences)
                     .setColor(colorRoles.getAccent())
                     .setLabel(""));
    }

    binding.chartPresence.setPieChartData(new PieChartData(values)
                                              .setHasCenterCircle(true)
                                              .setCenterCircleScale(0.75f)
                                              .setHasLabelsOnlyForSelected(true));
    binding.chartPresence.setChartRotation(-90, false);
    binding.chartPresence.setInteractive(false);
  }

}
