package com.tinf.qmobile.Adapter.Diarios;

import android.content.Context;
import android.os.Build;
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final DiariosViewHolder holder = (DiariosViewHolder) viewHolder;

        holder.max.setText(diarios.get(position).getMax());
        holder.nome.setText(diarios.get(position).getNome());
        holder.peso.setText(diarios.get(position).getPeso());
        holder.nota.setText(diarios.get(position).getNota());
        holder.tipo.setText(diarios.get(position).getTipo());
        holder.data.setText(diarios.get(position).getData());
    }

    @Override
    public int getItemCount() {
        return diarios.size();
    }
}
