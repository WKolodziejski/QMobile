package com.tinf.qmobile.holder.report;

import android.view.View;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.tinf.qmobile.databinding.TableCellCommonBinding;

public class TableCellViewHolder extends AbstractViewHolder {
    public TableCellCommonBinding binding;

    public TableCellViewHolder(View view) {
        super(view);
        binding = TableCellCommonBinding.bind(view);
    }

}
