package com.tinf.qacademico.Adapter.Calendario;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinf.qacademico.Class.Calendario.Evento;
import com.tinf.qacademico.R;
import com.tinf.qacademico.ViewHolder.EventosViewHolder;

import java.util.List;

public class EventosAdapter extends RecyclerView.Adapter {
    private List<Evento> eventos;
    private Context context;

    public EventosAdapter(List<Evento> eventos, Context context) {
        this.eventos = eventos;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new EventosViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_eventos, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final EventosViewHolder holder = (EventosViewHolder) viewHolder;

        holder.title.setText(eventos.get(i).getTitle());
        holder.description.setText(eventos.get(i).getDescription());
        holder.header.setBackgroundColor(eventos.get(i).getColor());

        if (eventos.get(i).getDescription().isEmpty()) {
            holder.description.setVisibility(View.GONE);
        }

        if (eventos.get(i).hasHappened()) {
            holder.description.append(" ACONTECEU");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.point.setBackgroundTintList(ColorStateList.valueOf(eventos.get(i).getColor()));

        }
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }
}
