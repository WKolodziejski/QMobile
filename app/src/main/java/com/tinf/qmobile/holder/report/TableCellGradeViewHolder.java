package com.tinf.qmobile.holder.report;

import static com.tinf.qmobile.utility.Design.getColorForGrade;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.TableCellGradeBinding;
import com.tinf.qmobile.model.matter.Matter;

public class TableCellGradeViewHolder extends TableBaseViewHolder {
    private final TableCellGradeBinding binding;

    public TableCellGradeViewHolder(@NonNull View view) {
        super(view);
        binding = TableCellGradeBinding.bind(view);
    }

    @Override
    public void bind(Context context, Matter matter, String cell) {
        binding.grade.setText(cell);

        if (cell.equals("-")) {
            binding.grade.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            binding.color.setCardBackgroundColor(context.getResources().getColor(R.color.transparent));
        } else {
            binding.grade.setTextColor(context.getResources().getColor(R.color.white));
            binding.color.setCardBackgroundColor(getColorForGrade(context, Float.parseFloat(cell)));
        }
    }

}
