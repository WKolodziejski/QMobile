package com.tinf.qmobile.adapter.journal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.holder.EtapasViewHolder;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period;

import java.util.ArrayList;
import java.util.List;

public class EtapasAdapter extends RecyclerView.Adapter {
    private List<Period> etapas;
    private Context context;

    public EtapasAdapter(Matter materia, Context context) {
        etapas = new ArrayList<>();

        for (int i = 0; i < materia.periods.size(); i++) {
            if (!materia.periods.get(i).journals.isEmpty()) {
                etapas.add(materia.periods.get(i));
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

        holder.title.setText(etapas.get(i).getTitle());
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setItemViewCacheSize(20);
        holder.recyclerView.setDrawingCacheEnabled(true);
        holder.recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        holder.recyclerView.setAdapter(new JournalAdapter(context, etapas.get(i).journals));
    }

    @Override
    public int getItemCount() {
        return etapas.size();
    }

    public void update(Matter matter) {
        etapas.clear();

        for (int i = 0; i < matter.periods.size(); i++) {
            if (!matter.periods.get(i).journals.isEmpty()) {
                etapas.add(matter.periods.get(i));
            }
        }
        
        notifyDataSetChanged();
    }

}
