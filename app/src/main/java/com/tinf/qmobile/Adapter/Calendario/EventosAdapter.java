package com.tinf.qmobile.Adapter.Calendario;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.Evento;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.EventosViewHolder;

import java.util.List;

public class EventosAdapter extends RecyclerView.Adapter {
    private List<Evento> eventos;
    private Context context;
    private boolean isSubList;

    public EventosAdapter(Context context, List<Evento> eventos, boolean isSubList) {
        this.eventos = eventos;
        this.context = context;
        this.isSubList = isSubList;
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

        if (eventos.get(i).getColor() == context.getResources().getColor(R.color.colorAccent)) { //não sei qual a cor certa peguei pelo inteiro dela
            holder.header.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
            holder.title.setTextColor(eventos.get(i).getColor());
            holder.description.setTextColor(eventos.get(i).getColor());
            holder.title.setTypeface(null, Typeface.BOLD);
        }

        if (eventos.get(i).getDescription().isEmpty()) {
            holder.description.setVisibility(View.GONE);
        }

        if (eventos.get(i).getHappened()) {
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.description.setPaintFlags(holder.description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.point.setBackgroundTintList(ColorStateList.valueOf(eventos.get(i).getColor()));
        }

        /*if (eventos.get(i).matter.getTarget() != null && !isSubList) {
            holder.header.setOnClickListener(v -> {
                Toast.makeText(context, eventos.get(i).matter.getTarget().getTitle(), Toast.LENGTH_SHORT).show();
            });
        }*/
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }
}
