package com.tinf.qmobile.Adapter.Diarios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinf.qmobile.Class.Materias.Etapa;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.EtapasViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EtapasAdapter extends RecyclerView.Adapter {
    private List<Etapa> etapas;
    private Context context;

    public EtapasAdapter(Materia materia, Context context) {
        etapas = new ArrayList<>();

        for (int i = 0; i < materia.etapas.size(); i++) {
            if (!materia.etapas.get(i).diarios.isEmpty()) {
                etapas.add(materia.etapas.get(i));
            }
        }

        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EtapasViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_etapas, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final EtapasViewHolder holder = (EtapasViewHolder) viewHolder;

        holder.title.setText(etapas.get(i).getEtapaName(context));
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setItemViewCacheSize(20);
        holder.recyclerView.setDrawingCacheEnabled(true);
        holder.recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        holder.recyclerView.setAdapter(new DiariosAdapter(etapas.get(i).diarios, context));
    }

    @Override
    public int getItemCount() {
        return etapas.size();
    }
}
