package com.qacademico.qacademico.Adapter.Materiais;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qacademico.qacademico.Class.Materiais.MateriaisList;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.ViewHolder.MateriaisListViewHolder;

import java.util.List;

public class MateriaisListAdapter extends RecyclerView.Adapter {
    private List<MateriaisList> materiaisList;
    private Context context;
    private OnDownloadRepassListener onDownloadRepassListener;

    public MateriaisListAdapter(List<MateriaisList> materiais, Context context) {
        this.materiaisList = materiais;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_materias, parent, false);
        return new MateriaisListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final MateriaisListViewHolder holder = (MateriaisListViewHolder) viewHolder;

        //holder.header.setBackgroundColor(materiaisList.get(i).getColor());
        holder.materia.setText(materiaisList.get(i).getTitle());

        MateriaisAdapter adapter = new MateriaisAdapter(materiaisList.get(i).getMateriais(), context);

        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false));

        adapter.setOnDownloadListener(link -> {
            onDownloadRepassListener.onDownload(link);
        });
    }

    @Override
    public int getItemCount() {
        return materiaisList.size();
    }

    public void setOnDowloadListener(OnDownloadRepassListener onDownloadRepassListener){
        this.onDownloadRepassListener = onDownloadRepassListener;
    }

    public interface OnDownloadRepassListener {
        void onDownload(String link);
    }

}
