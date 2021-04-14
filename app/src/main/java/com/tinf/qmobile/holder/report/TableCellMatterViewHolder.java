package com.tinf.qmobile.holder.report;

import android.view.View;
import androidx.annotation.NonNull;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.tinf.qmobile.databinding.TableCellMatterBinding;

public class TableCellMatterViewHolder extends AbstractViewHolder {
    public TableCellMatterBinding binding;

    public TableCellMatterViewHolder(@NonNull View view) {
        super(view);
        binding = TableCellMatterBinding.bind(view);
    }

}
