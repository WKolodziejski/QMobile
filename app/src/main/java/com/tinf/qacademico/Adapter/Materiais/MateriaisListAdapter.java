package com.tinf.qacademico.Adapter.Materiais;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.tinf.qacademico.Class.Materiais.MateriaisList;
import com.tinf.qacademico.R;
import com.tinf.qacademico.ViewHolder.MateriaisListViewHolder;

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

        FlexboxLayoutManager layout = new FlexboxLayoutManager(context);
        layout.setFlexDirection(FlexDirection.ROW);
        layout.setFlexWrap(FlexWrap.WRAP);
        layout.setJustifyContent(JustifyContent.FLEX_START);

        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.setLayoutManager(layout);

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
