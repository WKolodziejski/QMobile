package com.qacademico.qacademico.Adapter.Diarios;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qacademico.qacademico.Class.Diarios;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.ViewHolder.TrabalhoViewHolder;

import java.util.List;

public class DiariosAdapter extends RecyclerView.Adapter{
    private List<Diarios> diarios;
    private Context context;

    public DiariosAdapter(List<Diarios> diarios, Context context) {
        this.diarios = diarios;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_diarios, parent, false);
        return new TrabalhoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final TrabalhoViewHolder holder = (TrabalhoViewHolder) viewHolder;
        Diarios diarios = this.diarios.get(position) ;

        holder.max.setText(diarios.getMax());
        holder.nome.setText(diarios.getNome());
        holder.peso.setText(diarios.getPeso());
        holder.nota.setText(diarios.getNota());
        holder.tipo.setText(diarios.getTipo());
        holder.data.setText(diarios.getData());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.tipo.setTextColor(diarios.getTint());
        }
    }

    @Override
    public int getItemCount() {
        return diarios.size();
    }
}
