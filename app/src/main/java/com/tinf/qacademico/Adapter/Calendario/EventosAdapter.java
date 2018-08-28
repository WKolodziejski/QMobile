package com.tinf.qacademico.Adapter.Calendario;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final EventosViewHolder holder = (EventosViewHolder) viewHolder;

        holder.title.setText(eventos.get(position).getTitle());
        holder.description.setText(eventos.get(position).getDescription());
        holder.title.setTextColor(eventos.get(position).getColor());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.point.setBackgroundTintList(ColorStateList.valueOf(eventos.get(position).getColor()));
        }
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }
}
