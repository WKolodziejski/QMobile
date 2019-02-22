package com.tinf.qmobile.Adapter.Materiais;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tinf.qmobile.Class.Materiais.Material;
import com.tinf.qmobile.Fragment.MateriaisFragment;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.MateriaisViewHolder;
import java.util.List;

public class MateriaisAdapter extends RecyclerView.Adapter {
    private List<Material> materialList;
    private Context context;
    private MateriaisFragment.OnDownloadListener onDownloadListener;

    public MateriaisAdapter(Context context, List<Material> materialList, MateriaisFragment.OnDownloadListener onDownloadListener) {
        this.context = context;
        this.materialList = materialList;
        this.onDownloadListener = onDownloadListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_material, parent, false);
        return new MateriaisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final MateriaisViewHolder holder = (MateriaisViewHolder) viewHolder;

        holder.title.setText(materialList.get(i).getTitle());
        holder.date.setText((materialList.get(i).getDate()));
        holder.type.setImageDrawable(context.getResources().getDrawable(materialList.get(i).getIcon()));
        holder.offline.setVisibility(materialList.get(i).isDownloaded() ? View.VISIBLE : View.GONE);
        if (!materialList.get(i).getDescription().isEmpty()) {
            holder.description.setText(materialList.get(i).getDescription());
            holder.description.setVisibility(View.VISIBLE);
        }

        holder.title.setTag(i);

        holder.header.setOnClickListener(v -> {

            Integer pos = (Integer) holder.title.getTag();

            onDownloadListener.onDownload(materialList.get(pos));
        });
    }

    @Override
    public int getItemCount() {
        return materialList.size();
    }

}