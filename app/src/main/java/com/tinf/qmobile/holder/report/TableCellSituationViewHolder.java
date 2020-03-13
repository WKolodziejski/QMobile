package com.tinf.qmobile.holder.report;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.tinf.qmobile.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TableCellSituationViewHolder extends AbstractViewHolder {
    @BindView(R.id.table_cell_situation)     public TextView situation;

    public TableCellSituationViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

}