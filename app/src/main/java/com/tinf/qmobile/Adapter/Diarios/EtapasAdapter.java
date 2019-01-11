package com.tinf.qmobile.Adapter.Diarios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.DiariosViewHolder;
import com.tinf.qmobile.ViewHolder.EtapasViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EtapasAdapter extends RecyclerView.Adapter {
    private Materia materia;
    private Context context;

    public EtapasAdapter(Materia materia, Context context) {
        this.materia = materia;
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

        holder.title.setText(context.getResources().getString(materia.etapas.get(i).getEtapa()));
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        holder.recyclerView.setAdapter(new DiariosAdapter(materia.etapas.get(i).diarios, context));
    }

    @Override
    public int getItemCount() {
        return materia.etapas.size();
    }
}
