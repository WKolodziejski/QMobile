package com.tinf.qmobile.holder.report;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.tinf.qmobile.databinding.TableCellMatterBinding;
import com.tinf.qmobile.model.matter.Matter;

public class TableCellMatterViewHolder extends TableBaseViewHolder {
    private final TableCellMatterBinding binding;

    public TableCellMatterViewHolder(@NonNull View view) {
        super(view);
        binding = TableCellMatterBinding.bind(view);
    }

    @Override
    public void bind(Context context, Matter matter, String cell) {
        binding.matter.setText(cell);
    }

}
