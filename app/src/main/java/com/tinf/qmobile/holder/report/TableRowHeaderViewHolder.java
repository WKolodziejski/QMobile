package com.tinf.qmobile.holder.report;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.tinf.qmobile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TableRowHeaderViewHolder extends AbstractViewHolder {
    @BindView(R.id.table_row_color_badge)     public TextView badge;

    public TableRowHeaderViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

}
