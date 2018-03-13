package com.qacademico.qacademico;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cachapa.expandablelayout.ExpandableLayout;

public class ViewHolderHorario extends RecyclerView.ViewHolder {
    final TextView dia;
    final ExpandableLayout expand;
    final ImageView button;
    final LinearLayout expandAct;
    final RecyclerView recyclerView;
    final LinearLayout table;

    public ViewHolderHorario(View view) {
        super(view);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_horario_materias);
        dia = (TextView) view.findViewById(R.id.dia);
        expand = (ExpandableLayout) view.findViewById(R.id.expandable_layout_horario);
        button = (ImageView) view.findViewById(R.id.openHorario);
        expandAct = (LinearLayout) view.findViewById(R.id.openViewHorario);
        table = (LinearLayout) view.findViewById(R.id.tables_horario);
    }
}
