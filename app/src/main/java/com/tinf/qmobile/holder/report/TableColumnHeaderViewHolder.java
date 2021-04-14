package com.tinf.qmobile.holder.report;

import android.view.View;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.tinf.qmobile.databinding.TableColumnBinding;

public class TableColumnHeaderViewHolder extends AbstractViewHolder {
    public TableColumnBinding binding;

    public TableColumnHeaderViewHolder(View view) {
        super(view);
        binding = TableColumnBinding.bind(view);
    }

}
