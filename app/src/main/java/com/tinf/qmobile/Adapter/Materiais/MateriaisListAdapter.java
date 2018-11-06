package com.tinf.qmobile.Adapter.Materiais;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinf.qmobile.Class.Materiais.MateriaisList;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.MateriaisListViewHolder;

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

        /*FlexboxLayoutManager layout = new FlexboxLayoutManager(context);
        layout.setFlexDirection(FlexDirection.ROW);
        layout.setFlexWrap(FlexWrap.WRAP);
        layout.setJustifyContent(JustifyContent.FLEX_START);*/

        RecyclerView.LayoutManager layout = new LinearLayoutManager(context,
                RecyclerView.VERTICAL, false);

        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.setLayoutManager(layout);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(holder.recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);

        holder.recyclerView.addItemDecoration(dividerItemDecoration);

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
