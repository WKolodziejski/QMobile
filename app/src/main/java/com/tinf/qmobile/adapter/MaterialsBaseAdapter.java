package com.tinf.qmobile.adapter;

import static com.tinf.qmobile.network.Client.pos;

import android.content.Context;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.holder.material.MaterialBaseViewHolder;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.service.DownloadReceiver;
import com.tinf.qmobile.utility.UserUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.objectbox.Box;

public abstract class MaterialsBaseAdapter extends RecyclerView.Adapter<MaterialBaseViewHolder> {
  protected final Context context;
  private final List<Long> selected;
  protected final OnInteractListener listener;

  private final Box<Material> box = DataBase.get().getBoxStore().boxFor(Material.class);
  private final LongSparseArray<Long> downloading =
      DataBase.get().getMaterialsDataProvider().downloading;

  protected abstract List<Queryable> getList();

  public MaterialsBaseAdapter(Context context, OnInteractListener listener) {
    this.context = context;
    this.listener = listener;
    this.selected = new LinkedList<>();
  }

  protected final ActionMode.Callback callback = new ActionMode.Callback() {

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
                                                            context.getString(
                                                                R.string.contextual_remove_txt_plu),
                                                            selected.size()) : context.getString(
                R.string.contextual_remove_txt_sing))
            .setCancelable(true)
            .setPositiveButton(context.getString(R.string.dialog_delete), (dialogInterface, d) -> {
              if (listener.isSelectionMode()) {
                while (!selected.isEmpty()) {
                  long id = selected.get(0);

                  Material m = box.get(id);

                  File file = new File(m.getPath());

                  if (file.exists()) {
                    if (file.delete()) {
                      selected.remove(id);
                      box.put(m);
                    } else {
                      Log.d("File NOT removed", m.getFileName());
                      Toast.makeText(context, context.getString(R.string.toast_delete_file_fail),
                                     Toast.LENGTH_SHORT).show();
                    }
                  } else {
                    Log.d("File doesn't exist", m.getFileName());
                    Toast.makeText(context, context.getString(R.string.toast_delete_file_fail),
                                   Toast.LENGTH_SHORT).show();
                  }

                  selected.remove(id);
                }
                listener.onSelectedCount(selected.size());
              }
            })
            .setNegativeButton(context.getString(R.string.dialog_cancel), null)
            .create()
            .show();

        return true;

      } else if (menuItem.getItemId() == R.id.action_select) {
        for (int i = 0; i < getList().size(); i++)
          if (getList().get(i) instanceof Material) {
            Material material = ((Material) getList().get(i));

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
      for (int i = 0; i < getList().size(); i++)
        if (getList().get(i) instanceof Material) {
          ((Material) getList().get(i)).isSelected = false;
          notifyItemChanged(i);
        }
      listener.onDestroyActionMode(actionMode);
    }

  };

  public void notifyItemDownloaded(long download) {
    Long id = downloading.get(download);

    if (id != null)
      for (int i = 0; i < getList().size(); i++)
        if (getList().get(i) instanceof Material) {
          Material material = (Material) getList().get(i);

          if (material.id == id) {
            material.isDownloaded = true;
            material.isDownloading = false;
            material.see();
            notifyItemChanged(i);
            break;
          }
        }
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
                                  UserUtils.getYear(pos) + "/" + UserUtils.getPeriod(pos)),
                    box.put(material));

    Log.d(material.getTitle(), "Downloading...");
  }

  public void handleDownload(int i) {
    Material material = (Material) getList().get(i);

    if (material.isDownloaded) {
      DownloadReceiver.openFile(
          UserUtils.getYear(pos) + "/" + UserUtils.getPeriod(pos) + "/" + material.getFileName());
    } else {
      material.isDownloading = true;
      download(material);
    }

    notifyItemChanged(i);
  }

  public int highlight(long id) {
    for (int i = 0; i < getList().size(); i++) {
      Queryable q = getList().get(i);

      if (q instanceof Material) {
        Material m = (Material) q;

        if (m.id == id) {
          m.highlight = true;
          notifyItemChanged(i);
          return i;
        }
      }
    }

    return -1;
  }

}
