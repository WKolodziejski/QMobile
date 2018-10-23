package com.tinf.qacademico.Adapter.Calendario;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.tinf.qacademico.Class.Calendario.Dia;
import com.tinf.qacademico.R;
import com.tinf.qacademico.ViewHolder.CalendarioViewHolder;
import java.util.ArrayList;
import java.util.List;

public class CalendarioAdapter extends RecyclerView.Adapter {
    private List<Dia> calendarioList;
    private Context context;

    public CalendarioAdapter(List<Dia> calendarioList, Context context) {
        this.calendarioList = clearList(calendarioList);
        this.context = context;
    }

    public void update(List<Dia> calendarioList) {
        if (calendarioList != null) {
            this.calendarioList = clearList(calendarioList);
        } else {
            this.calendarioList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    private List<Dia> clearList(List<Dia> list) {
        List<Dia> clear = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).eventos.isEmpty()) {
                clear.add(list.get(i));
            }
        }
        return clear;
    }

    public List<Dia> getCalendarioList() {
        return calendarioList;
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

        holder.dia.setText(String.valueOf(calendarioList.get(position).getDay()));
        holder.eventos.setAdapter(new EventosAdapter(calendarioList.get(position).eventos, context));
        holder.eventos.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        holder.layout.setTag(position);
    }

    @Override
    public int getItemCount() {
        return calendarioList.size();
    }
}
