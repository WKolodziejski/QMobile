package com.tinf.qmobile.adapter;

import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.HEADER;
import static com.tinf.qmobile.model.ViewType.IGNORE;
import static com.tinf.qmobile.model.ViewType.MATERIAL;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.database.OnData;
import com.tinf.qmobile.holder.material.MaterialEmptyViewHolder;
import com.tinf.qmobile.holder.material.MaterialBaseViewHolder;
import com.tinf.qmobile.holder.material.MaterialViewHolder;
import com.tinf.qmobile.holder.material.MatterViewHolder;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.material.Material;

import java.util.List;

public class MaterialsAdapter extends MaterialsBaseAdapter implements OnData<Queryable> {
    private final AsyncListDiffer<Queryable> materials;

    public MaterialsAdapter(Context context, OnInteractListener listener) {
        super(context, listener);
        this.materials = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<Queryable>() {
            @Override
            public boolean areItemsTheSame(@NonNull Queryable oldItem, @NonNull Queryable newItem) {
                boolean equals = oldItem.getId() == newItem.getId() && oldItem.getItemType() == newItem.getItemType();

                if (equals && oldItem.getItemType() == MATERIAL) {
                    Material oldMaterial = ((Material) oldItem);
                    Material newMaterial = ((Material) newItem);

                    newMaterial.isDownloading = oldMaterial.isDownloading;
                    //newMaterial.isDownloaded = oldMaterial.isDownloaded;
                    newMaterial.isSelected = oldMaterial.isSelected;
                    newMaterial.highlight = oldMaterial.highlight;
                }

                return equals;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Queryable oldItem, @NonNull Queryable newItem) {
                return oldItem.isSame(newItem);
            }
        });

        onUpdate(DataBase.get().getMaterialsDataProvider().getList());
    }

    @Override
    public int getItemViewType(int i) {
        return materials.getCurrentList().get(i).getItemType();
    }

    @NonNull
    @Override
    public MaterialBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                return new MatterViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.material_header, parent, false));

            case MATERIAL:
                return new MaterialViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.material_item, parent, false));

            case EMPTY:
                return new MaterialEmptyViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.material_empty, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialBaseViewHolder holder, int position) {
        holder.bind(context, listener, this, callback, materials.getCurrentList().get(position));
    }

    @Override
    public int getItemCount() {
        return materials.getCurrentList().size();
    }

    @Override
    protected List<Queryable> getList() {
        return materials.getCurrentList();
    }

    @Override
    public void onUpdate(List<Queryable> list) {
        materials.submitList(list);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        DataBase.get().getMaterialsDataProvider().removeOnDataListener(this);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        DataBase.get().getMaterialsDataProvider().addOnDataListener(this);
    }

}
