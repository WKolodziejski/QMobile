package com.qacademico.qacademico.Adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qacademico.qacademico.Class.Etapa;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.ViewHolder.EtapaViewHolder;

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
                .inflate(R.layout.list_diarios, parent, false);
        //EtapaViewHolder holder = new EtapaViewHolder(view);
        return new EtapaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final EtapaViewHolder holder = (EtapaViewHolder) viewHolder;
        Etapa etapa = etapas.get(position) ;

        holder.aux.setText(etapa.getAux());

        RecyclerView.LayoutManager layout = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);

        holder.recyclerView.setAdapter(new TrabalhoAdapter(etapa.getTrabalhoList(), context));

        holder.recyclerView.setLayoutManager(layout);
    }

    @Override
    public int getItemCount() {
        return etapas.size();
    }
}
