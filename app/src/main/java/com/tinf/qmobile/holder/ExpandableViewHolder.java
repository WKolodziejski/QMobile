package com.tinf.qmobile.holder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExpandableViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.exp_view_title)          public TextView title;
    @BindView(R.id.exp_layout_expand)       public ExpandableLayout expand;
    @BindView(R.id.exp_view_color_badge)    public TextView badge;
    @BindView(R.id.exp_view_header_layout)  public ConstraintLayout layout;
    @BindView(R.id.exp_view_expand_img)     public ImageView arrow;
    @BindView(R.id.exp_recycler)            public RecyclerView recyclerView;
    @BindView(R.id.exp_details)             public TextView details;

    public ExpandableViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

}
