package com.qacademico.qacademico.ViewHolder;

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

public class DiariosViewHolder extends RecyclerView.ViewHolder{
    public final TextView materia;
    public final ExpandableLayout expand;
    public final ImageView button;
    public final LinearLayout expandAct, nothing;
    public final RecyclerView recyclerView;
    public final LinearLayout table;

    public DiariosViewHolder(View view) {
        super(view);

        materia = (TextView) view.findViewById(R.id.materiaDiarios);
        expand = (ExpandableLayout) view.findViewById(R.id.expandable_layout_diarios);
        button = (ImageView) view.findViewById(R.id.openDiarios);
        expandAct = (LinearLayout) view.findViewById(R.id.openViewDiarios);
        nothing = (LinearLayout) view.findViewById(R.id.diarios_nothing);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_diarios_etapa);
        table = (LinearLayout) view.findViewById(R.id.tables_diarios);
    }
}
