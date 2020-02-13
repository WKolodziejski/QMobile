package com.tinf.qmobile.holder.report;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.tinf.qmobile.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TableCellViewHolder extends AbstractViewHolder {
    @BindView(R.id.table_cell_text)     public TextView text;

    public TableCellViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

}
