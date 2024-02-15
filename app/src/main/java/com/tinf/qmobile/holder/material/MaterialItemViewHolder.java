package com.tinf.qmobile.holder.material;

import static com.tinf.qmobile.network.Client.pos;

import android.content.Context;
import android.view.ActionMode;
import android.view.View;

import androidx.annotation.NonNull;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.MaterialsBaseAdapter;
import com.tinf.qmobile.adapter.OnMaterialInteractListener;
import com.tinf.qmobile.databinding.MaterialItemBinding;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.service.DownloadReceiver;
import com.tinf.qmobile.utility.ColorsUtils;
import com.tinf.qmobile.utility.DesignUtils;
import com.tinf.qmobile.utility.UserUtils;

import java.io.File;

public class MaterialItemViewHolder extends MaterialBaseViewHolder<Material> {
  private final MaterialItemBinding binding;

  public MaterialItemViewHolder(
      @NonNull
      View view) {
    super(view);
    binding = MaterialItemBinding.bind(view);
  }

  @Override
  public void bind(Context context,
                   OnMaterialInteractListener listener,
                   MaterialsBaseAdapter adapter,
                   ActionMode.Callback callback,
                   Material material,
                   boolean isHeader) {
    material.isDownloaded =
        new File(DownloadReceiver.getMaterialPath(material.getFileName())).exists();

    binding.icon.setImageDrawable(DesignUtils.getDrawable(context, material.getIcon()));

    binding.title.setText(material.getTitle());
    binding.date.setText(material.getDateString());
    binding.offline.setVisibility(
        material.isDownloaded && !material.isDownloading ? View.VISIBLE : View.INVISIBLE);
    binding.loading.setVisibility(
        material.isDownloading && !material.isDownloaded ? View.VISIBLE : View.INVISIBLE);

    if (material.isSelected) {
      itemView.setBackgroundColor(ColorsUtils.getColor(context,
                                                       com.google.android.material.R.attr.colorSecondaryContainer));
      setTextColor(ColorsUtils.getColor(context,
                                        com.google.android.material.R.attr.colorOnSecondaryContainer));
    } else if (!material.isSeen_()) {
      itemView.setBackgroundColor(
          ColorsUtils.getColor(context, com.google.android.material.R.attr.colorPrimaryContainer));
      setTextColor(ColorsUtils.getColor(context,
                                        com.google.android.material.R.attr.colorOnPrimaryContainer));
    } else {
      itemView.setBackgroundColor(
          ColorsUtils.getColor(context, R.color.transparent));
      setTextColor(ColorsUtils.getColor(context,
                                        com.google.android.material.R.attr.colorOnSurface));
    }

    if (!material.getDescription()
                 .isEmpty()) {
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
          DownloadReceiver.openFile(UserUtils.getYear(pos) + "/" + UserUtils.getPeriod(pos) + "/" +
                                    material.getFileName());
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
      itemView.setBackgroundColor(
          ColorsUtils.getColor(context, com.google.android.material.R.attr.colorPrimaryContainer));
      setTextColor(ColorsUtils.getColor(context,
                                        com.google.android.material.R.attr.colorOnPrimaryContainer));
    }
  }

  private void setTextColor(int color) {
    binding.title.setTextColor(color);
    binding.date.setTextColor(color);
  }

}
