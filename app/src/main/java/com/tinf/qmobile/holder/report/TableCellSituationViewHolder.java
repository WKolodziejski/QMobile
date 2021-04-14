package com.tinf.qmobile.holder.report;

import android.view.View;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.tinf.qmobile.databinding.TableCellSituationBinding;

public class TableCellSituationViewHolder extends AbstractViewHolder {
    public TableCellSituationBinding binding;

    public TableCellSituationViewHolder(View view) {
        super(view);
        binding = TableCellSituationBinding.bind(view);
    }

}