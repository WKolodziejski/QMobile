package com.tinf.qmobile.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.holder.materials.MaterialBaseViewHolder;
import com.tinf.qmobile.holder.materials.MaterialHeaderViewHolder;
import com.tinf.qmobile.holder.materials.MaterialViewHolder;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.material.Material_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.query.QueryBuilder;
import io.objectbox.reactive.DataObserver;
import static com.tinf.qmobile.model.Queryable.ViewType.HEADER;
import static com.tinf.qmobile.model.Queryable.ViewType.MATERIAL;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.utility.User.REGISTRATION;

public class MaterialsAdapter extends RecyclerView.Adapter<MaterialBaseViewHolder> implements OnUpdate {
    private List<Queryable> materials;
    private Context context;
    private OnDownloadListener onDownload;

    public MaterialsAdapter(Context context, Bundle bundle, MaterialsAdapter.OnDownloadListener onDownload) {
        this.context = context;
        this.onDownload = onDownload;
        this.materials = getList(bundle);

        Client.get().addOnUpdateListener(this);

        BoxStore boxStore = DataBase.get().getBoxStore();

        DataObserver observer = data -> {
            List<Queryable> updated = getList(bundle);

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return materials.size();
                }

                @Override
                public int getNewListSize() {
                    return updated.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    if (materials.get(oldItemPosition) instanceof Matter && updated.get(newItemPosition) instanceof Matter)
                        return (((Matter) materials.get(oldItemPosition)).id == (((Matter) updated.get(newItemPosition)).id));

                    else if (materials.get(oldItemPosition) instanceof Material && updated.get(newItemPosition) instanceof Material)
                        return (((Material) materials.get(oldItemPosition)).id == (((Material) updated.get(newItemPosition)).id));

                    else return false;
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return (materials.get(oldItemPosition).equals(updated.get(newItemPosition)));
                }

            }, true);

            materials.clear();
            materials.addAll(updated);
            result.dispatchUpdatesTo(this);
        };

        boxStore.subscribe(Material.class)
                .on(AndroidScheduler.mainThread())
                .onlyChanges()
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        boxStore.subscribe(Matter.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);
    }

    private List<Queryable> getList(Bundle bundle) {

        List<Queryable> list = new ArrayList<>();

        if (bundle == null) {
            List<Matter> matters = new ArrayList<>(DataBase.get().getBoxStore()
                    .boxFor(Matter.class)
                    .query()
                    .order(Matter_.title_)
                    .equal(Matter_.year_, User.getYear(pos))
                    .and()
                    .equal(Matter_.period_, User.getPeriod(pos))
                    .build()
                    .find());

            for (int i = 0; i < matters.size(); i++) {
                if (!matters.get(i).materials.isEmpty()) {
                    list.add(matters.get(i));
                    list.addAll(matters.get(i).materials);
                }
            }
        } else {
            QueryBuilder<Material> builder = DataBase.get().getBoxStore()
                    .boxFor(Material.class).query();

            builder.link(Material_.matter)
                    .equal(Matter_.id, bundle.getLong("ID"));

            list.addAll(builder.build().find());
        }

        DataBase.get().getBoxStore().runInTx(() -> {

            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + "/QMobile/" + User.getCredential(REGISTRATION) + "/" + User.getYear(pos) + "/" + User.getPeriod(pos));

            if (folder.exists()) {
                if (folder.listFiles() != null) {

                    List<File> files = Arrays.asList(folder.listFiles());

                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i) instanceof Material) {
                            Material material = (Material) list.get(i);

                            for (int j = 0; j < files.size(); j++) {
                                if (material.getFileName().equals(files.get(j).getName())) {
                                    material.isDownloaded = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });
        return list;
    }

    @Override
    public int getItemViewType(int position) {
        return materials.get(position).getItemType();
    }

    @NonNull
    @Override
    public MaterialBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                return new MaterialHeaderViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.material_header, parent, false));

            case MATERIAL:
                return new MaterialViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.material_item, parent, false));

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialBaseViewHolder holder, int position) {
        holder.bind(context, materials.get(position), onDownload, this);
    }

    @Override
    public int getItemCount() {
        return materials.size();
    }

    @Override
    public void onScrollRequest() {

    }

    @Override
    public void onDateChanged() {
        materials = getList(null);
        notifyDataSetChanged();
    }

    public interface OnDownloadListener {
        void onDownload(Material material);
    }

}
