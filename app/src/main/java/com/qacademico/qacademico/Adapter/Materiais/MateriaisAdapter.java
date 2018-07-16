package com.qacademico.qacademico.Adapter.Materiais;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.qacademico.qacademico.Class.Materiais;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.ViewHolder.MateriaisViewHolder;
import java.util.List;

public class MateriaisAdapter extends RecyclerView.Adapter {
    private List<Materiais> materialList;
    private Context context;
    private OnDownloadListener onDownloadListener;

    public MateriaisAdapter(List<Materiais> materialList, Context context) {
        this.materialList = materialList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_material, parent, false);
        return new MateriaisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final MateriaisViewHolder holder = (MateriaisViewHolder) viewHolder;

        holder.title.setText(materialList.get(position).getNomeConteudo());
        holder.date.setText((materialList.get(position).getData()));
        holder.info.setText(materialList.get(position).getDescricao());
        holder.img.setImageDrawable(materialList.get(position).getIcon());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.img.setImageTintList(ColorStateList.valueOf(materialList.get(position).getTint()));
        }

        holder.title.setTag(position);

        holder.btn.setOnClickListener(v -> {

            Integer pos = (Integer) holder.title.getTag();

            onDownloadListener.onDownload(materialList.get(pos).getLink());
        });
    }

    @Override
    public int getItemCount() {
        return materialList.size();
    }

    public void setOnDownloadListener(OnDownloadListener onDownloadListener){
        this.onDownloadListener = onDownloadListener;
    }

    public interface OnDownloadListener {
        void onDownload(String link);
    }
}