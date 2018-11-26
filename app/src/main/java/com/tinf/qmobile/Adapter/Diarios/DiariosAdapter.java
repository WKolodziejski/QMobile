package com.tinf.qmobile.Adapter.Diarios;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tinf.qmobile.Class.Materias.Diarios;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.DiariosViewHolder;

import java.util.List;

public class DiariosAdapter extends RecyclerView.Adapter {
    private List<Diarios> diarios;
    private Context context;

    public DiariosAdapter(List<Diarios> diarios, Context context) {
        this.diarios = diarios;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DiariosViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_diarios, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final DiariosViewHolder holder = (DiariosViewHolder) viewHolder;

        holder.title.setText(diarios.get(i).getNome());
        holder.type.setText(diarios.get(i).getTipo());
        holder.date.setText(diarios.get(i).getData());
        holder.type.setTextColor(context.getResources().getColor(diarios.get(i).etapa.getTarget().materia.getTarget().getColor()));
        holder.weight.setText(String.format(context.getResources().getString(
                R.string.diarios_Peso), diarios.get(i).getPeso()));
        holder.grade.setText(String.format(context.getResources().getString(
                R.string.diarios_Nota), diarios.get(i).getNota(), diarios.get(i).getMax()));

    }

    @Override
    public int getItemCount() {
        return diarios.size();
    }
}
