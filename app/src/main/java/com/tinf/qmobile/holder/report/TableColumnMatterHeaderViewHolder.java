package com.tinf.qmobile.holder.report;

import android.view.View;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.tinf.qmobile.databinding.TableColumnMatterBinding;

public class TableColumnMatterHeaderViewHolder extends AbstractViewHolder {
    public TableColumnMatterBinding binding;

    public TableColumnMatterHeaderViewHolder(View view) {
        super(view);
        binding = TableColumnMatterBinding.bind(view);
    }

}
