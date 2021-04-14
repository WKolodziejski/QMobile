package com.tinf.qmobile.holder.report;

import android.view.View;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.tinf.qmobile.databinding.TableRowBinding;

public class TableRowHeaderViewHolder extends AbstractViewHolder {
    public TableRowBinding binding;

    public TableRowHeaderViewHolder(View view) {
        super(view);
        binding = TableRowBinding.bind(view);
    }

}
