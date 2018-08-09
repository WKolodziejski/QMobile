package com.qacademico.qacademico.ViewHolder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qacademico.qacademico.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExpandableListViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.list_text)               public TextView text;
    @BindView(R.id.expandable_layout_list)  public ExpandableLayout expand;
    @BindView(R.id.list_arrow)              public ImageView arrow;
    @BindView(R.id.open_list)               public LinearLayout expandAct;
    @BindView(R.id.list_nothing)            public ConstraintLayout nothing;
    @BindView(R.id.recycler_list)           public RecyclerView recyclerView;
    @BindView(R.id.table_list)              public LinearLayout table;

    public ExpandableListViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
