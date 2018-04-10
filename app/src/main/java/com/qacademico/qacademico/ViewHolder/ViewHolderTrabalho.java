package com.qacademico.qacademico.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qacademico.qacademico.R;

public class ViewHolderTrabalho extends RecyclerView.ViewHolder {
    public final TextView nome;
    public final TextView peso;
    public final TextView max;
    public final TextView nota;
    public final TextView tipo;
    public final LinearLayout header;

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
