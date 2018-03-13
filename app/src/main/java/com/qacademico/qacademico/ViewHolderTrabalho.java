package com.qacademico.qacademico;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewHolderTrabalho extends RecyclerView.ViewHolder {
    final TextView nome;
    final TextView peso;
    final TextView max;
    final TextView nota;
    final TextView tipo;
    final LinearLayout header;

    public ViewHolderTrabalho(View view) {
        super(view);

        nome = (TextView) view.findViewById(R.id.diarios_nome);
        peso = (TextView) view.findViewById(R.id.diarios_peso);
        max = (TextView) view.findViewById(R.id.diarios_max);
        nota = (TextView) view.findViewById(R.id.diarios_nota);
        tipo = (TextView) view.findViewById(R.id.diarios_tipo);
        header = (LinearLayout) view.findViewById(R.id.diarios_header);
    }
}
