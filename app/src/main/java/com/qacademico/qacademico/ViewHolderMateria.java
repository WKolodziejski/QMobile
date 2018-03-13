package com.qacademico.qacademico;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ViewHolderMateria extends RecyclerView.ViewHolder {
    final TextView hora;
    final TextView materia;

    public ViewHolderMateria(View view) {
        super(view);

        hora = (TextView) view.findViewById(R.id.horario_hora);
        materia = (TextView) view.findViewById(R.id.horario_materia);
    }
}
