package com.qacademico.qacademico;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.cachapa.expandablelayout.ExpandableLayout;

public class ViewHolderDiarios extends RecyclerView.ViewHolder{
    final TextView materia;
    final ExpandableLayout expand;
    final ImageView button;
    final LinearLayout expandAct, nothing;
    final RecyclerView recyclerView;
    final LinearLayout table;

    public ViewHolderDiarios(View view) {
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
