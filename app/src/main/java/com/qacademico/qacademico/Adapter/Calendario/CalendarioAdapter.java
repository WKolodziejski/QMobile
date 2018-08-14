package com.qacademico.qacademico.Adapter.Calendario;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.qacademico.qacademico.Class.Calendario.Dia;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.ViewHolder.CalendarioViewHolder;

import java.util.List;

public class CalendarioAdapter extends RecyclerView.Adapter {
    private List<Dia> calendarioList;
    private Context context;

    public CalendarioAdapter(List<Dia> calendarioList, Context context) {
        this.calendarioList = calendarioList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new CalendarioViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_calendario, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final CalendarioViewHolder holder = (CalendarioViewHolder) viewHolder;

        holder.dia.setText(Integer.toString(calendarioList.get(position).getDia()));
        holder.eventos.setAdapter(new EventosAdapter(calendarioList.get(position).getEventos(), context));
        holder.eventos.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public int getItemCount() {
        return calendarioList.size();
    }
}
