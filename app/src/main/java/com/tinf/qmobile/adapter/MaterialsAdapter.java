package com.tinf.qmobile.adapter;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.holder.journal.EmptyViewHolder;
import com.tinf.qmobile.holder.MaterialBaseViewHolder;
import com.tinf.qmobile.model.Material_;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.Material;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.query.QueryBuilder;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;
import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.tinf.qmobile.BuildConfig.APPLICATION_ID;
import static com.tinf.qmobile.model.Queryable.ViewType.EMPTY;
import static com.tinf.qmobile.model.Queryable.ViewType.HEADER;
import static com.tinf.qmobile.model.Queryable.ViewType.MATERIAL;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.utility.User.REGISTRATION;

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

                                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                            + "/QMobile/" + User.getCredential(REGISTRATION) + "/" + User.getYear(pos) + "/" + User.getPeriod(pos) + "/" + m.getFileName());

                                    Log.d("File", m.getFileName());

                                    if (file.exists()) {
                                        if (file.delete()) {
                                            selected.remove(id);
                                            m.setMime("");
                                            m.setPath("");
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
                        notifyItemChanged(i);
                        openFile(material);
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
        if (materials.isEmpty())
            return EMPTY;
        else
            return materials.get(position).getItemType();
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
        if (!materials.isEmpty())
            holder.bind(materials.get(position));
    }

    @Override
    public int getItemCount() {
        if (materials.isEmpty())
            return 1;
        else
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

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        sub1.cancel();
        sub2.cancel();
    }

    private void openFile(Material material) {
        Intent intent = new Intent(ACTION_VIEW);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + material.getPath() + "/" + material.getFileName());

        Log.d("Fragment", file.getPath());

        Uri uri = FileProvider.getUriForFile(context, APPLICATION_ID, file);

        intent.setDataAndType(uri, material.getMime());

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getResources().getString(R.string.text_no_handler), Toast.LENGTH_LONG).show();
        }
    }

    private void downloadMaterial(Material material) {
        if (Client.isConnected()) {
            DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

            long downloadID = manager.enqueue(Client.get().downloadMaterial(material));

            Log.d("DOWNLOAD", String.valueOf(downloadID));

            material.setMime(manager.getMimeTypeForDownloadedFile(downloadID));
            material.setPath("/QMobile/" + User.getCredential(REGISTRATION) + "/" + User.getYear(pos) + "/" + User.getPeriod(pos));
            material.see();

            downloading.put(downloadID, box.put(material));

            Toast.makeText(context, context.getResources().getString(R.string.materiais_downloading), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.client_no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void selectItem(Material material) {
        if (material.isSelected) {
            selected.remove(material.id);
            material.isSelected = false;
        } else {
            selected.add(material.id);
            material.isSelected = true;
        }
        listener.onSelectedCount(selected.size());
    }

    public class MatterViewHolder extends MaterialBaseViewHolder<Matter> {
        @BindView(R.id.material_title)           public TextView title;
        @BindView(R.id.material_color_badge)     public TextView badge;

        public MatterViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void bind(Matter matter) {
            title.setText(matter.getTitle());
            badge.setBackgroundTintList(ColorStateList.valueOf(matter.getColor()));

            int n = matter.getMaterialNotSeenCount();

            if (n > 0) {
                badge.setText(String.valueOf(n));
            } else {
                badge.setText("");
            }

            itemView.setOnClickListener(view -> {
                if (!listener.isSelectionMode()) {
                    Intent intent = new Intent(context, MatterActivity.class);
                    intent.putExtra("ID", matter.id);
                    intent.putExtra("PAGE", MatterActivity.MATERIALS);
                    context.startActivity(intent);
                }
            });
        }

    }

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
        public void bind(Material material) {
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
                if (listener.isSelectionMode()) {
                    if (material.isDownloaded)
                        selectItem(material);
                } else {
                    if (material.isDownloaded) {
                        openFile(material);
                    } else {
                        material.isDownloading = true;
                        downloadMaterial(material);
                    }
                }
                notifyItemChanged(getAdapterPosition());
            });

            itemView.setOnLongClickListener(view -> {
                if (material.isDownloaded) {
                    if (!listener.isSelectionMode())
                        listener.setSelectionMode(callback);

                    selectItem(material);
                    notifyItemChanged(getAdapterPosition());
                    return true;

                } else return false;
            });
        }
    }

    public interface OnInteractListener {
        boolean isSelectionMode();
        void setSelectionMode(ActionMode.Callback callback);
        void onSelectedCount(int size);
        boolean onCreateActionMode(ActionMode actionMode, Menu menu);
        void onDestroyActionMode(ActionMode actionMode);
    }

}
