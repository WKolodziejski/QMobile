package com.tinf.qmobile.holder.report;

import android.content.Context;
import android.view.View;

import com.tinf.qmobile.databinding.TableCellCommonBinding;
import com.tinf.qmobile.model.matter.Matter;

public class TableCellViewHolder extends TableBaseViewHolder {
    private final TableCellCommonBinding binding;

    public TableCellViewHolder(View view) {
        super(view);
        binding = TableCellCommonBinding.bind(view);
    }

    @Override
    public void bind(Context context, Matter matter, String cell) {
        binding.text.setText(cell);
    }

}
