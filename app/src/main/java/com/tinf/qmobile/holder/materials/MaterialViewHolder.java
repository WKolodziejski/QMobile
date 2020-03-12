package com.tinf.qmobile.holder.materials;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.MaterialsAdapter;
import com.tinf.qmobile.model.material.Material;
import butterknife.BindView;
import butterknife.ButterKnife;

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
    public void bind(Context context, Material material, MaterialsAdapter.OnInteractListener listener, MaterialsAdapter adapter) {
        icon.setImageDrawable(context.getDrawable(material.getIcon()));

        title.setText(material.getTitle());
        date.setText(material.getDateString());
        offline.setVisibility(material.isDownloaded ? View.VISIBLE : View.GONE);
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
            loading.setVisibility(listener.onClick(material) ? View.VISIBLE : View.GONE);
            if (material.isSelected) {
                itemView.setBackgroundColor(context.getResources().getColor(R.color.selectionBackground));
            } else {
                itemView.setBackgroundColor(material.isSeen_() ? context.getResources().getColor(R.color.transparent) : context.getResources().getColor(R.color.notificationBackground));
            }
        });

        itemView.setOnLongClickListener(view -> {
            boolean r = listener.onLongClick(material);
            if (material.isSelected) {
                itemView.setBackgroundColor(context.getResources().getColor(R.color.selectionBackground));
            } else {
                itemView.setBackgroundColor(material.isSeen_() ? context.getResources().getColor(R.color.transparent) : context.getResources().getColor(R.color.notificationBackground));
            }
            return r;
        });
    }

}
