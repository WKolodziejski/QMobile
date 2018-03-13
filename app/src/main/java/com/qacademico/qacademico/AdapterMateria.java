package com.qacademico.qacademico;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class AdapterMateria extends RecyclerView.Adapter {
    private List<Materia> materias;
    private Context context;

    public AdapterMateria(List<Materia> materias, Context context) {
        this.materias = materias;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_horario, parent, false);
        ViewHolderMateria holder = new ViewHolderMateria(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolderMateria holder = (ViewHolderMateria) viewHolder;
        Materia materia = materias.get(position) ;

        holder.hora.setText(materia.getHora());
        holder.materia.setText(materia.getMateria());
    }

    @Override
    public int getItemCount() {
        return materias.size();
    }
}
