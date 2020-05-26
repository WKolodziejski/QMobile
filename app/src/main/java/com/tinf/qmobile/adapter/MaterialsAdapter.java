package com.tinf.qmobile.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.holder.material.EmptyViewHolder;
import com.tinf.qmobile.holder.material.MaterialBaseViewHolder;
import com.tinf.qmobile.holder.material.MaterialViewHolder;
import com.tinf.qmobile.holder.material.MatterViewHolder;
import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.material.Material_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.service.DownloadReceiver;
import com.tinf.qmobile.utility.User;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.query.QueryBuilder;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.HEADER;
import static com.tinf.qmobile.model.ViewType.MATERIAL;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.service.DownloadReceiver.PATH;

public class MaterialsAdapter extends RecyclerView.Adapter<MaterialBaseViewHolder> implements OnUpdate {
    private List<Queryable> materials;
    private List<Long> selected;
    private LongSparseArray<Long> downloading;
    private Context context;
    private OnInteractListener listener;
    private DataSubscription sub1, sub2;

    private Box<Material> box = DataBase.get().getBoxStore().boxFor(Material.class);

    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return listener.onCreateActionMode(actionMode, menu);
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.action_delete) {
                new MaterialAlertDialogBuilder(context)
                        .setTitle(context.getString(R.string.dialog_delete))
                        .setMessage(selected.size() > 1 ? String.format(Locale.getDefault(),
                                context.getString(R.string.contextual_remove_txt_plu),
                                String.valueOf(selected.size())) : context.getString(R.string.contextual_remove_txt_sing))
                        .setCancelable(true)
                        .setPositiveButton(context.getString(R.string.dialog_delete), (dialogInterface, d) -> {
                            if (listener.isSelectionMode()) {

                                while (!selected.isEmpty()) {
                                    long id = selected.get(0);

                                    Material m = box.get(id);

                                    File file = new File(m.getPath());

                                    Log.d("File", m.getFileName());

                                    if (file.exists()) {
                                        if (file.delete()) {
                                            selected.remove(id);
                                            box.put(m);
                                        }
                                    } else break;
                                }

                                listener.onSelectedCount(selected.size());
                            }
                        })
                        .setNegativeButton(context.getString(R.string.dialog_cancel), null)
                        .create()
                        .show();

                return true;

            } else if (menuItem.getItemId() == R.id.action_select) {
                for (int i = 0; i < materials.size(); i++)
                    if (materials.get(i) instanceof Material) {
                        Material material = ((Material) materials.get(i));

                        if (material.isDownloaded) {
                            if (!selected.contains(material.id)) {
                                selected.add(material.id);
                                material.isSelected = true;
                                notifyItemChanged(i);
                            }
                        }
                    }

                listener.onSelectedCount(selected.size());

                return true;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            selected.clear();
            for (int i = 0; i < materials.size(); i++)
                if (materials.get(i) instanceof Material) {
                    ((Material) materials.get(i)).isSelected = false;
                    notifyItemChanged(i);
                }
            listener.onDestroyActionMode(actionMode);
        }
    };

    public MaterialsAdapter(Context context, Bundle bundle, OnInteractListener listener) {
        this.context = context;
        this.listener = listener;

        this.materials = getList(bundle);

        this.selected = new ArrayList<>();
        this.downloading = new LongSparseArray();

        Client.get().addOnUpdateListener(this);

        BoxStore boxStore = DataBase.get().getBoxStore();

        DataObserver observer = data -> update(bundle);

        sub1 = boxStore.subscribe(Material.class)
                .on(AndroidScheduler.mainThread())
                .onlyChanges()
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        sub2 = boxStore.subscribe(Matter.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);
    }

    public void notifyItemDownloaded(long download) {
        Long id = downloading.get(download);

        if (id != null)
            for (int i = 0; i < materials.size(); i++)
                if (materials.get(i) instanceof Material) {
                    Material material = (Material) materials.get(i);

                    if (material.id == id) {
                        material.isDownloaded = true;
                        material.isDownloading = false;
                        material.see();
                        notifyItemChanged(i);
                        break;
                    }
                }
    }

    private void update(Bundle bundle) {
        List<Queryable> updated = getList(bundle);

        if (bundle == null) {
            for (int i = 0; i < materials.size(); i++) {
                if (materials.get(i) instanceof Material) {
                    Material m1 = ((Material) materials.get(i));

                    for (Queryable q : updated)
                        if (q instanceof Material) {
                            Material m2 = (Material) q;

                            if (m1.id == m2.id) {
                                m2.isDownloading = m1.isDownloading;
                                m2.isSelected = m1.isSelected;
                                break;
                            }
                        }
                }
            }
        }

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
            public boolean areItemsTheSame(int o, int n) {
                if (materials.get(o) instanceof Matter && updated.get(n) instanceof Matter)
                    return (((Matter) materials.get(o)).id == (((Matter) updated.get(n)).id));

                else if (materials.get(o) instanceof Material && updated.get(n) instanceof Material)
                    return (((Material) materials.get(o)).id == (((Material) updated.get(n)).id));

                else return materials.get(o) instanceof Empty && updated.get(n) instanceof Empty;
            }

            @Override
            public boolean areContentsTheSame(int o, int n) {
                return (materials.get(o).equals(updated.get(n)));
            }

        }, true);

        materials.clear();
        materials.addAll(updated);
        result.dispatchUpdatesTo(this);
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

        if (list.isEmpty())
            list.add(new Empty());

        return list;
    }

    @Override
    public int getItemViewType(int i) {
        return materials.get(i).getItemType();
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
                return new EmptyViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.material_empty, parent, false));

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialBaseViewHolder holder, int position) {
        holder.bind(context, listener, this, callback, materials.get(position));
    }

    @Override
    public int getItemCount() {
        return materials.size();
    }

    @Override
    public void onScrollRequest() { }

    @Override
    public void onDateChanged() {
        materials = getList(null);
        notifyDataSetChanged();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        sub1.cancel();
        sub2.cancel();
    }

    public void selectItem(Material material) {
        if (material.isSelected) {
            selected.remove(material.id);
            material.isSelected = false;
        } else {
            selected.add(material.id);
            material.isSelected = true;
        }
        listener.onSelectedCount(selected.size());
    }

    public void download(Material material) {
        material.see();
        downloading.put(DownloadReceiver
                        .download(context,
                                material.getLink(),
                                material.getFileName(),
                                User.getYear(pos) + "/" + User.getPeriod(pos)),
                box.put(material));
    }

    public interface OnInteractListener {
        boolean isSelectionMode();
        void setSelectionMode(ActionMode.Callback callback);
        void onSelectedCount(int size);
        boolean onCreateActionMode(ActionMode actionMode, Menu menu);
        void onDestroyActionMode(ActionMode actionMode);
    }

}
