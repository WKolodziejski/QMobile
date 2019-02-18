package com.tinf.qmobile.Adapter.Materiais;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tinf.qmobile.Class.Materiais.Materiais;
import com.tinf.qmobile.Fragment.MateriaisFragment;
import com.tinf.qmobile.R;
import com.tinf.qmobile.ViewHolder.MateriaisViewHolder;
import java.util.List;

public class MateriaisAdapter extends RecyclerView.Adapter {
    private List<Materiais> materialList;
    private Context context;
    private MateriaisFragment.OnDownloadListener onDownloadListener;

    public MateriaisAdapter(Context context, List<Materiais> materialList, MateriaisFragment.OnDownloadListener onDownloadListener) {
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final MateriaisViewHolder holder = (MateriaisViewHolder) viewHolder;

        holder.title.setText(materialList.get(position).getNomeConteudo());
        holder.date.setText((materialList.get(position).getData()));
        holder.type.setImageDrawable(context.getResources().getDrawable(materialList.get(position).getIcon()));
        if (!materialList.get(position).getDescricao().isEmpty()) {
            holder.description.setText(materialList.get(position).getDescricao());
            holder.description.setVisibility(View.VISIBLE);
        }

        holder.title.setTag(position);

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