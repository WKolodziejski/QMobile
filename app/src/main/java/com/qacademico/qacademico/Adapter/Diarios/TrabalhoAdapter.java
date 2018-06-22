package com.qacademico.qacademico.Adapter.Diarios;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Class.Trabalho;
import com.qacademico.qacademico.ViewHolder.TrabalhoViewHolder;

import java.util.List;

public class TrabalhoAdapter extends RecyclerView.Adapter{
    private List<Trabalho> trabalhos;
    private Context context;

    public TrabalhoAdapter(List<Trabalho> trabalhos, Context context) {
        this.trabalhos = trabalhos;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_diarios_item, parent, false);
        return new TrabalhoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final TrabalhoViewHolder holder = (TrabalhoViewHolder) viewHolder;
        Trabalho trabalho = trabalhos.get(position) ;

        holder.max.setText(trabalho.getMax());
        holder.nome.setText(trabalho.getNome());
        holder.peso.setText(trabalho.getPeso());
        holder.nota.setText(trabalho.getNota());
        holder.tipo.setText(trabalho.getTipo());
        holder.data.setText(trabalho.getData());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.tipo.setTextColor(trabalho.getTint());
        }
    }

    @Override
    public int getItemCount() {
        return trabalhos.size();
    }
}
