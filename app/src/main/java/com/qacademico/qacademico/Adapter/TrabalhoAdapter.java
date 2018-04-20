package com.qacademico.qacademico.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_diarios_item, parent, false);
        TrabalhoViewHolder holder = new TrabalhoViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final TrabalhoViewHolder holder = (TrabalhoViewHolder) viewHolder;
        Trabalho trabalho = trabalhos.get(position) ;

        holder.max.setText(trabalho.getMax());
        holder.nome.setText(trabalho.getNome());
        holder.peso.setText(trabalho.getPeso());
        holder.nota.setText(trabalho.getNota());
        holder.tipo.setText(trabalho.getTipo());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable color = context.getResources().getDrawable(R.drawable.layout_bg_header_left);
            color.setTint(trabalho.getTint());
            holder.header.setBackground(color);
        }
    }

    @Override
    public int getItemCount() {
        return trabalhos.size();
    }
}
