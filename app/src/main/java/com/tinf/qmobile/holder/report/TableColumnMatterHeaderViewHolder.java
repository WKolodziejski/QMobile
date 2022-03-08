package com.tinf.qmobile.holder.report;

import android.content.Context;
import android.view.View;

import com.tinf.qmobile.databinding.TableColumnMatterBinding;
import com.tinf.qmobile.model.matter.Matter;

public class TableColumnMatterHeaderViewHolder extends TableBaseViewHolder {
    private final TableColumnMatterBinding binding;

    public TableColumnMatterHeaderViewHolder(View view) {
        super(view);
        binding = TableColumnMatterBinding.bind(view);
    }

    @Override
    public void bind(Context context, Matter matter, String cell) {
        binding.matter.setText(cell);
    }

}
