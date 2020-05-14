package com.tinf.qmobile.holder.material;

import android.content.Context;
import android.os.Environment;
import android.view.ActionMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.MaterialsAdapter;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.service.DownloadReceiver;
import com.tinf.qmobile.utility.User;

import java.io.File;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.service.DownloadReceiver.PATH;

public class MaterialViewHolder extends MaterialBaseViewHolder<Material> {
    @BindView(R.id.materiais_type)          public ImageView icon;
    @BindView(R.id.materiais_title)         public TextView title;
    @BindView(R.id.materiais_date)          public TextView date;
    @BindView(R.id.materiais_description)   public TextView description;
    @BindView(R.id.material_offline)        public ImageView offline;
    @BindView(R.id.material_loading)        public ProgressBar loading;

    public MaterialViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Context context, MaterialsAdapter.OnInteractListener listener, MaterialsAdapter adapter, ActionMode.Callback callback, Material material) {
        material.isDownloaded = new File(DownloadReceiver.getMaterialPath(material.getFileName())).exists();

        icon.setImageDrawable(context.getDrawable(material.getIcon()));

        title.setText(material.getTitle());
        date.setText(material.getDateString());
        offline.setVisibility(material.isDownloaded && !material.isDownloading? View.VISIBLE : View.GONE);
        loading.setVisibility(material.isDownloading ? View.VISIBLE : View.GONE);

        if (material.isSelected) {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.selectionBackground));
        } else {
            itemView.setBackgroundColor(material.isSeen_() ? context.getResources().getColor(R.color.transparent) : context.getResources().getColor(R.color.notificationBackground));
        }

        if (!material.getDescription().isEmpty()) {
            description.setText(material.getDescription());
            description.setVisibility(View.VISIBLE);
        } else {
            description.setText(material.getDescription());
            description.setVisibility(View.GONE);
        }

        itemView.setOnClickListener(view -> {
            if (listener.isSelectionMode()) {
                if (material.isDownloaded)
                    adapter.selectItem(material);
            } else {
                if (material.isDownloaded) {
                    DownloadReceiver.openFile(User.getYear(pos) + "/" + User.getPeriod(pos) + "/" + material.getFileName());
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
    }
}
