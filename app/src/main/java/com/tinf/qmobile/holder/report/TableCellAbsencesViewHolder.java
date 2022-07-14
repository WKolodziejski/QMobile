package com.tinf.qmobile.holder.report;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.TableCellAbsencesBinding;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.utility.ColorUtils;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.SliceValue;

public class TableCellAbsencesViewHolder extends TableBaseViewHolder {
    private final TableCellAbsencesBinding binding;

    public TableCellAbsencesViewHolder(@NonNull View view) {
        super(view);
        binding = TableCellAbsencesBinding.bind(view);
    }

    @Override
    public void bind(Context context, Matter matter, String cell) {
        int color1 = matter.getColor();
        int color2 = ColorUtils.INSTANCE.contrast(color1, 0.25f);
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
                .setColor(context.getResources().getColor(R.color.colorPrimaryDark))
                .setLabel(""));

        if (presences > 0) {
            values.add(new SliceValue(presences)
                    .setColor(color1)
                    .setLabel(""));
        }

        if (absences > 0) {
            values.add(new SliceValue(absences)
                    .setColor(color2)
                    .setLabel(""));
        }

        binding.chartPresence.setPieChartData(new PieChartData(values)
                .setHasCenterCircle(true)
                .setCenterCircleScale(0.75f)
                .setHasLabelsOnlyForSelected(true));
        binding.chartPresence.setChartRotation(-90, false);
        binding.chartPresence.setInteractive(false);

//        if (absences > 0) {
//            binding.chartPresence.selectValue(
//                    new SelectedValue(presences > 0 ? 2 : 1, 0, SelectedValue.SelectedValueType.LINE));
//        }
    }

}
