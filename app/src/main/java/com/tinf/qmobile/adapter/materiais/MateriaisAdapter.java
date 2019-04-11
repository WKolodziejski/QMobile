package com.tinf.qmobile.adapter.materiais;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tinf.qmobile.model.materiais.Material;
import com.tinf.qmobile.fragment.MateriaisFragment;
import com.tinf.qmobile.R;
import com.tinf.qmobile.holder.MateriaisViewHolder;
import java.util.List;

public class MateriaisAdapter extends RecyclerView.Adapter {
    private List<Material> materials;
    private Context context;
    private MateriaisFragment.OnDownloadListener onDownloadListener;

    public MateriaisAdapter(Context context, List<Material> materials, MateriaisFragment.OnDownloadListener onDownloadListener) {
        this.context = context;
        this.materials = materials;
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

        holder.title.setText(materials.get(i).getTitle());
        holder.date.setText((materials.get(i).getDateString()));
        holder.type.setImageDrawable(context.getResources().getDrawable(materials.get(i).getIcon()));
        holder.offline.setVisibility(materials.get(i).isDownloaded ? View.VISIBLE : View.GONE);
        if (!materials.get(i).getDescription().isEmpty()) {
            holder.description.setText(materials.get(i).getDescription());
            holder.description.setVisibility(View.VISIBLE);
        }

        holder.title.setTag(i);

        holder.header.setOnClickListener(v -> {

            Integer pos = (Integer) holder.title.getTag();

            onDownloadListener.onDownload(materials.get(pos));
        });
    }

    @Override
    public int getItemCount() {
        return materials.size();
    }

}