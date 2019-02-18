package com.tinf.qmobile.Adapter.Materiais;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinf.qmobile.Class.Materiais.Materiais;
import com.tinf.qmobile.Class.Materiais.MateriaisList;
import com.tinf.qmobile.Fragment.MateriaisFragment;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.MateriaisListViewHolder;

import java.util.List;

public class MateriaisListAdapter extends RecyclerView.Adapter {
    private List<MateriaisList> materiaisList;
    private Context context;
    private MateriaisFragment.OnDownloadListener onDownloadListener;

    public MateriaisListAdapter(Context context, List<MateriaisList> materiais, MateriaisFragment.OnDownloadListener onDownloadListener) {
        this.context = context;
        this.materiaisList = materiais;
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

        holder.materia.setText(materiaisList.get(i).getTitle());
        holder.materia.setTag(i);

        MateriaisAdapter adapter = new MateriaisAdapter(context, materiaisList.get(i).getMateriais(), onDownloadListener);

        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,
                RecyclerView.VERTICAL, false));
    }

    @Override
    public int getItemCount() {
        return materiaisList.size();
    }

    public void update(List<MateriaisList> materiaisList){
        this.materiaisList = materiaisList;
        notifyDataSetChanged();
    }

}
