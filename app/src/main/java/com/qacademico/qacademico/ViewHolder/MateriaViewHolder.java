package com.qacademico.qacademico.ViewHolder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.qacademico.qacademico.R;

public class MateriaViewHolder extends RecyclerView.ViewHolder {
    public final TextView hora;
    public final TextView materia;

    public MateriaViewHolder(View view) {
        super(view);

        hora = (TextView) view.findViewById(R.id.horario_hora);
        materia = (TextView) view.findViewById(R.id.horario_materia);
    }
}
