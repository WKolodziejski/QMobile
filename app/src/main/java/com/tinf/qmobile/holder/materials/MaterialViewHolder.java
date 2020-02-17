package com.tinf.qmobile.holder.materials;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.MaterialsAdapter;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.model.material.Material;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MaterialViewHolder extends MaterialBaseViewHolder<Material> {
    @BindView(R.id.materiais_type)          public ImageView icon;
    @BindView(R.id.materiais_title)         public TextView title;
    @BindView(R.id.materiais_date)          public TextView date;
    @BindView(R.id.materiais_description)   public TextView description;
    @BindView(R.id.materiais_header)        public ConstraintLayout header;
    @BindView(R.id.materiais_offline)       public ImageView offline;

    public MaterialViewHolder(@NonNull View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void bind(Context context, Material material, MaterialsAdapter.OnDownloadListener onDownload, MaterialsAdapter adapter) {
        icon.setImageDrawable(context.getDrawable(material.getIcon()));

        title.setText(material.getTitle());
        date.setText(material.getDateString());
        offline.setVisibility(material.isDownloaded ? View.VISIBLE : View.GONE);

        if (!material.getDescription().isEmpty()) {
            description.setText(material.getDescription());
            description.setVisibility(View.VISIBLE);
        } else {
            description.setText(material.getDescription());
            description.setVisibility(View.GONE);
        }

        if (material.isSeen_()) {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        } else {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.notificationBackground));
            material.see();
            DataBase.get().getBoxStore().boxFor(Material.class).put(material);
        }

        header.setOnClickListener(view -> {
            onDownload.onDownload(material);
        });
    }

}
