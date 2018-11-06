package com.tinf.qmobile.ViewHolder;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinf.qmobile.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiariosListViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.list_text)               public TextView text;
    @BindView(R.id.expandable_layout_list)  public ExpandableLayout expand;
    @BindView(R.id.list_arrow)              public ImageView arrow;
    @BindView(R.id.open_list)               public LinearLayout expandAct;
    @BindView(R.id.list_nothing)            public ConstraintLayout nothing;
    @BindView(R.id.recycler_list_diarios)   public RecyclerView recyclerView;
    @BindView(R.id.table_list)              public LinearLayout table;

    public DiariosListViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
