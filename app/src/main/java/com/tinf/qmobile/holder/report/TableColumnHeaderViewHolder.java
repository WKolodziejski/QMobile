package com.tinf.qmobile.holder.report;

import android.content.Context;
import android.view.View;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.tinf.qmobile.databinding.TableColumnBinding;
import com.tinf.qmobile.model.matter.Matter;

public class TableColumnHeaderViewHolder extends TableBaseViewHolder {
    private final TableColumnBinding binding;

    public TableColumnHeaderViewHolder(View view) {
        super(view);
        binding = TableColumnBinding.bind(view);
    }

    @Override
    public void bind(Context context, Matter matter, String cell) {
        binding.text.setText(cell);
    }

}
