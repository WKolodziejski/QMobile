package com.tinf.qmobile.holder;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinf.qmobile.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiariosListViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.list_title)              public TextView title;
    @BindView(R.id.expandable_layout_list)  public ExpandableLayout expand;
    @BindView(R.id.headerIndicator)         public ImageView arrow;
    @BindView(R.id.diarios_open)            public ImageView open;
    @BindView(R.id.open_list)               public ConstraintLayout expandAct;
    @BindView(R.id.recycler_list_diarios)   public RecyclerView recyclerView;
    @BindView(R.id.diarios_nothing)         public View nothing;

    public DiariosListViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}