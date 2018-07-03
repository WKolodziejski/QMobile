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

public class ExpandableListViewHolder extends RecyclerView.ViewHolder {
    public final TextView text;
    public final ExpandableLayout expand;
    public final ImageView arrow;
    public final LinearLayout expandAct;
    public final ConstraintLayout nothing;
    public final RecyclerView recyclerView;
    public final LinearLayout table;

    public ExpandableListViewHolder(View view) {
        super(view);

        text = (TextView) view.findViewById(R.id.list_text);
        expand = (ExpandableLayout) view.findViewById(R.id.expandable_layout_list);
        arrow = (ImageView) view.findViewById(R.id.list_arrow);
        expandAct = (LinearLayout) view.findViewById(R.id.open_list);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_list);
        table = (LinearLayout) view.findViewById(R.id.table_list);
        nothing = (ConstraintLayout) view.findViewById(R.id.list_nothing);
    }
}
