package com.tinf.qmobile.adapter.materiais;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.fragment.MateriaisFragment;
import com.tinf.qmobile.R;
import com.tinf.qmobile.holder.MateriaisListViewHolder;

import java.util.List;

public class MateriaisListAdapter extends RecyclerView.Adapter {
    private List<Matter> materiaList;
    private Context context;
    private MateriaisFragment.OnDownloadListener onDownloadListener;

    public MateriaisListAdapter(Context context, List<Matter> materiaList, MateriaisFragment.OnDownloadListener onDownloadListener) {
        this.context = context;
        this.materiaList = materiaList;
        this.onDownloadListener = onDownloadListener;
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

        holder.materia.setText(materiaList.get(i).getTitle());
        holder.materia.setTag(i);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setAdapter(new MateriaisAdapter(context, materiaList.get(i).materials, onDownloadListener));
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public int getItemCount() {
        return materiaList.size();
    }

    public void update(List<Matter> materiaList){
        this.materiaList = materiaList;
        notifyDataSetChanged();
    }

}
