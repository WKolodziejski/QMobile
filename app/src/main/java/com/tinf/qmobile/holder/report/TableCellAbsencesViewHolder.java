package com.tinf.qmobile.holder.report;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.tinf.qmobile.databinding.TableCellAbsencesBinding;
import com.tinf.qmobile.model.matter.Matter;

public class TableCellAbsencesViewHolder extends TableBaseViewHolder {
    private final TableCellAbsencesBinding binding;

    public TableCellAbsencesViewHolder(@NonNull View view) {
        super(view);
        binding = TableCellAbsencesBinding.bind(view);
    }

    @Override
    public void bind(Context context, Matter matter, String cell) {
        int classes = matter.getClassesGiven();
        int absences = matter.getAbsences();
        int presences = classes - absences;
        int percentage = classes <= 0 ? 0 : (int) ((float) presences / classes * 100f);

        binding.absences.setText(matter.getAbsencesString());
        binding.progress.setIndicatorColor(matter.getColor());
        binding.progress.setProgress(percentage);
        binding.progress.setVisibility(classes > 0 ? VISIBLE : INVISIBLE);
    }

}
