package com.tinf.qmobile.adapter.materiais;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.fragment.MateriaisFragment;
import com.tinf.qmobile.holder.MateriaisListViewHolder;
import com.tinf.qmobile.model.matter.Matter;

import java.util.List;

public class MateriaisListAdapter extends RecyclerView.Adapter {
    private List<Matter> materiaList;
    private Context context;
    private MateriaisFragment.OnDownloadListener onDownloadListener;
    private boolean isBundle;

    public MateriaisListAdapter(Context context, List<Matter> materiaList, boolean isBundle, MateriaisFragment.OnDownloadListener onDownloadListener) {
        this.context = context;
        this.materiaList = materiaList;
        this.onDownloadListener = onDownloadListener;
        this.isBundle = isBundle;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_materiais, parent, false);
        return new MateriaisListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final MateriaisListViewHolder holder = (MateriaisListViewHolder) viewHolder;

        holder.materia.setVisibility(isBundle ? View.GONE : View.VISIBLE);
        holder.materia.setText(materiaList.get(i).getTitle());
        holder.materia.setTag(i);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setAdapter(new MateriaisAdapter(context, materiaList.get(i).materials, j -> {
            onDownloadListener.onDownload(i, j);
        }));
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public int getItemCount() {
        return materiaList.size();
    }

    public void update(List<Matter> materiaList) {
        this.materiaList = materiaList;
        notifyDataSetChanged();
    }

    public void notifyDownloaded(int i) {
        notifyItemChanged(i);
    }

}
