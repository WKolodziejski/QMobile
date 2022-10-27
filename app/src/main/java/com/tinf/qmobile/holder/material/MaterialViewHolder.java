package com.tinf.qmobile.holder.material;

import static com.tinf.qmobile.network.Client.pos;

import android.content.Context;
import android.view.ActionMode;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.MaterialsBaseAdapter;
import com.tinf.qmobile.adapter.OnInteractListener;
import com.tinf.qmobile.databinding.MaterialItemBinding;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.service.DownloadReceiver;
import com.tinf.qmobile.utility.UserUtils;

import java.io.File;

public class MaterialViewHolder extends MaterialBaseViewHolder<Material> {
    private final MaterialItemBinding binding;

    public MaterialViewHolder(@NonNull View view) {
        super(view);
        binding = MaterialItemBinding.bind(view);
    }

    @Override
    public void bind(Context context, OnInteractListener listener, MaterialsBaseAdapter adapter, ActionMode.Callback callback, Material material, boolean isHeader) {
        material.isDownloaded = new File(DownloadReceiver.getMaterialPath(material.getFileName())).exists();

        binding.icon.setImageDrawable(AppCompatResources.getDrawable(context, material.getIcon()));

        binding.title.setText(material.getTitle());
        binding.date.setText(material.getDateString());
        binding.offline.setVisibility(material.isDownloaded && !material.isDownloading ? View.VISIBLE : View.INVISIBLE);
        binding.loading.setVisibility(material.isDownloading && !material.isDownloaded ? View.VISIBLE : View.INVISIBLE);

        if (material.isSelected) {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.selectionBackground));
        } else {
            itemView.setBackgroundColor(material.isSeen_() ? context.getResources().getColor(R.color.transparent) : context.getResources().getColor(R.color.notificationBackground));
        }

        if (!material.getDescription().isEmpty()) {
            binding.description.setText(material.getDescription());
            binding.description.setVisibility(View.VISIBLE);
        } else {
            binding.description.setText(material.getDescription());
            binding.description.setVisibility(View.GONE);
        }

        itemView.setOnClickListener(view -> {
            if (listener.isSelectionMode()) {
                if (material.isDownloaded)
                    adapter.selectItem(material);
            } else {
                if (material.isDownloaded) {
                    DownloadReceiver.openFile(UserUtils.getYear(pos) + "/" + UserUtils.getPeriod(pos) + "/" + material.getFileName());
                } else {
                    material.isDownloading = true;
                    adapter.download(material);
                }
            }
            adapter.notifyItemChanged(getAdapterPosition());
        });

        itemView.setOnLongClickListener(view -> {
            if (material.isDownloaded) {
                if (!listener.isSelectionMode())
                    listener.setSelectionMode(callback);

                adapter.selectItem(material);
                adapter.notifyItemChanged(getAdapterPosition());
                return true;

            } else return false;
        });

        if (material.highlight) {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.notificationBackground));
        }
    }

}
