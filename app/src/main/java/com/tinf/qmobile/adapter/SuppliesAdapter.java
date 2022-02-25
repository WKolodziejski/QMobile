package com.tinf.qmobile.adapter;

import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.HEADER;
import static com.tinf.qmobile.model.ViewType.MATERIAL;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.holder.material.MaterialBaseViewHolder;
import com.tinf.qmobile.holder.material.MaterialEmptyViewHolder;
import com.tinf.qmobile.holder.material.MaterialViewHolder;
import com.tinf.qmobile.holder.material.MatterViewHolder;
import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.material.Material_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import io.objectbox.query.QueryBuilder;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

public class SuppliesAdapter extends MaterialsBaseAdapter {
    private final AsyncListDiffer<Queryable> list;
    private final DataSubscription sub1;
    private final DataSubscription sub2;
    private final Handler handler;

    public SuppliesAdapter(Context context, Bundle bundle, OnInteractListener listener) {
        super(context, listener);
        this.handler = new Handler(Looper.getMainLooper());
        this.list = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<Queryable>() {
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

        updateList(bundle);

        DataObserver observer = data -> list.submitList(getList(bundle));

        sub1 = DataBase.get().getBoxStore().subscribe(Material.class)
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);

        sub2 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);
    }

    private void updateList(Bundle bundle) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Queryable> list = getList(bundle);
            handler.post(() -> this.list.submitList(list));
        });
    }

    private List<Queryable> getList(Bundle bundle) {
        QueryBuilder<Material> builder = DataBase.get().getBoxStore()
                .boxFor(Material.class).query();

        builder.link(Material_.matter)
                .equal(Matter_.id, bundle.getLong("ID"));

        List<Queryable> list = new ArrayList<>(builder.build().find());

        if (list.isEmpty())
            list.add(new Empty());

        return list;
    }

    @Override
    public int getItemViewType(int i) {
        return list.getCurrentList().get(i).getItemType();
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
        holder.bind(context, listener, this, callback, list.getCurrentList().get(position));
    }

    @Override
    public int getItemCount() {
        return list.getCurrentList().size();
    }

    @Override
    protected List<Queryable> getList() {
        return list.getCurrentList();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        sub1.cancel();
        sub2.cancel();
    }

}

