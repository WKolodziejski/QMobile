package com.qacademico.qacademico;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class AdapterEtapa extends RecyclerView.Adapter {
    private List<Etapa> etapas;
    private Context context;

    public AdapterEtapa(List<Etapa> etapa, Context context) {
        this.etapas = etapa;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_diarios, parent, false);
        //ViewHolderEtapa holder = new ViewHolderEtapa(view);
        return new ViewHolderEtapa(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolderEtapa holder = (ViewHolderEtapa) viewHolder;
        Etapa etapa = etapas.get(position) ;

        holder.aux.setText(etapa.getAux());

        RecyclerView.LayoutManager layout = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);

        holder.recyclerView.setAdapter(new AdapterTrabalho(etapa.getTrabalhoList(), context));

        holder.recyclerView.setLayoutManager(layout);
    }

    @Override
    public int getItemCount() {
        return etapas.size();
    }
}
