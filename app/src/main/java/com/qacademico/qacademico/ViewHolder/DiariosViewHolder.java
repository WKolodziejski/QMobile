package com.qacademico.qacademico.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qacademico.qacademico.R;

public class DiariosViewHolder extends RecyclerView.ViewHolder {
    public final TextView nome;
    public final TextView peso;
    public final TextView max;
    public final TextView nota;
    public final TextView tipo;
    public final TextView data;

    public DiariosViewHolder(View view) {
        super(view);

        nome = (TextView) view.findViewById(R.id.diarios_nome);
        peso = (TextView) view.findViewById(R.id.diarios_peso);
        max = (TextView) view.findViewById(R.id.diarios_max);
        nota = (TextView) view.findViewById(R.id.diarios_nota);
        tipo = (TextView) view.findViewById(R.id.diarios_tipo);
        data = (TextView) view.findViewById(R.id.diarios_data);
    }
}
