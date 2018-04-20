package com.qacademico.qacademico.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qacademico.qacademico.R;

import net.cachapa.expandablelayout.ExpandableLayout;

public class HorarioViewHolder extends RecyclerView.ViewHolder {
    public final TextView dia;
    public final ExpandableLayout expand;
    public final ImageView button;
    public final LinearLayout expandAct;
    public final RecyclerView recyclerView;
    public final LinearLayout table;

    public HorarioViewHolder(View view) {
        super(view);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_horario_materias);
        dia = (TextView) view.findViewById(R.id.dia);
        expand = (ExpandableLayout) view.findViewById(R.id.expandable_layout_horario);
        button = (ImageView) view.findViewById(R.id.openHorario);
        expandAct = (LinearLayout) view.findViewById(R.id.openViewHorario);
        table = (LinearLayout) view.findViewById(R.id.tables_horario);
    }
}
