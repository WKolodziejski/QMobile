package com.tinf.qmobile.Adapter.Diarios;

import android.content.Context;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinf.qmobile.Class.Materias.Etapa;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.EtapaViewHolder;

import java.util.List;

public class EtapaAdapter extends RecyclerView.Adapter {
    private List<Etapa> etapas;
    private Context context;

    public EtapaAdapter(List<Etapa> etapa, Context context) {
        this.etapas = etapa;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_etapa, parent, false);
        return new EtapaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final EtapaViewHolder holder = (EtapaViewHolder) viewHolder;
        Etapa etapa = etapas.get(position) ;

        holder.etapa.setText(etapa.getEtapa());

        RecyclerView.LayoutManager layout = new LinearLayoutManager(context,
                RecyclerView.VERTICAL, false);

        holder.recyclerView.setAdapter(new DiariosAdapter(etapa.diarios, context));

        holder.recyclerView.setLayoutManager(layout);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(holder.recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        holder.recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public int getItemCount() {
        return etapas.size();
    }
}