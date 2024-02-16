package com.tinf.qmobile.adapter;

import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.HEADER;
import static com.tinf.qmobile.model.ViewType.MATERIAL;
import static com.tinf.qmobile.model.ViewType.MATTER;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kodmap.library.kmrecyclerviewstickyheader.KmStickyListener;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.database.OnData;
import com.tinf.qmobile.database.OnList;
import com.tinf.qmobile.holder.material.MaterialBaseViewHolder;
import com.tinf.qmobile.holder.material.MaterialEmptyViewHolder;
import com.tinf.qmobile.holder.material.MaterialHeaderViewHolder;
import com.tinf.qmobile.holder.material.MaterialItemViewHolder;
import com.tinf.qmobile.holder.material.MaterialMatterViewHolder;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.Header;
import com.tinf.qmobile.model.material.Material;

import java.util.List;

public class MaterialsAdapter extends MaterialsBaseAdapter
    implements KmStickyListener, OnData<Queryable> {
  private final AsyncListDiffer<Queryable> list;
  private final OnList<Queryable> onList;
  private int currentHeader;

  public MaterialsAdapter(Context context, OnMaterialInteractListener listener, OnList<Queryable> onList) {
    super(context, listener);
    this.onList = onList;
    this.list = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<Queryable>() {
      @Override
      public boolean areItemsTheSame(
          @NonNull
          Queryable oldItem,
          @NonNull
          Queryable newItem) {
        boolean equals =
            oldItem.getId() == newItem.getId() && oldItem.getItemType() == newItem.getItemType();

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
      public boolean areContentsTheSame(
          @NonNull
          Queryable oldItem,
          @NonNull
          Queryable newItem) {
        return oldItem.isSame(newItem);
      }
    });

    onUpdate(DataBase.get().getMaterialsDataProvider().getList());
  }

  @Override
  public int getItemViewType(int i) {
    return list.getCurrentList().get(i).getItemType();
  }

  @NonNull
  @Override
  public MaterialBaseViewHolder onCreateViewHolder(
      @NonNull
      ViewGroup parent, int viewType) {
    switch (viewType) {
      case MATTER:
        return new MaterialMatterViewHolder(LayoutInflater.from(context)
                                                          .inflate(R.layout.material_header, parent,
                                                                   false));

      case MATERIAL:
        return new MaterialItemViewHolder(LayoutInflater.from(context)
                                                        .inflate(R.layout.material_item, parent,
                                                             false));

      case HEADER:
        return new MaterialHeaderViewHolder(LayoutInflater.from(context)
                                                          .inflate(R.layout.header_empty, parent,
                                                                   false));

      case EMPTY:
        return new MaterialEmptyViewHolder(LayoutInflater.from(context)
                                                         .inflate(R.layout.material_empty, parent,
                                                                  false));
    }

    return null;
  }

  @Override
  public void onBindViewHolder(
      @NonNull
      MaterialBaseViewHolder holder, int i) {
    holder.bind(context, listener, this, callback, list.getCurrentList().get(i),
                i == currentHeader);
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
  public void onUpdate(List<Queryable> list) {
    this.list.submitList(list);
    onList.onUpdate(list);
  }

  @Override
  public void onDetachedFromRecyclerView(
      @NonNull
      RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    DataBase.get().getMaterialsDataProvider().removeOnDataListener(this);
  }

  @Override
  public void onAttachedToRecyclerView(
      @NonNull
      RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    DataBase.get().getMaterialsDataProvider().addOnDataListener(this);
  }

  @Override
  public Integer getHeaderPositionForItem(Integer i) {
    Queryable q = list.getCurrentList().get(i);

    while (!(q instanceof Header) && i > 0)
      q = list.getCurrentList().get(--i);

    notifyItemChanged(currentHeader);
    currentHeader = i + 1;
    notifyItemChanged(currentHeader);

    return i < 0 ? 0 : i;
  }

  @Override
  public Integer getHeaderLayout(Integer i) {
    if (list.getCurrentList().get(i) instanceof Header)
      return R.layout.journal_header_color;
    else
      return R.layout.header_empty;
  }

  @Override
  public void bindHeaderData(View header, Integer i) {
    if (!(list.getCurrentList().get(i) instanceof Header))
      return;

    Header h = (Header) list.getCurrentList().get(i);

    int n = h.getJournalNotSeenCount();

    TextView b = header.findViewById(R.id.badge);
    b.setText(n > 0 ? String.valueOf(n) : "");
    b.setBackgroundTintList(ColorStateList.valueOf(h.getColor()));
  }

  @Override
  public Boolean isHeader(Integer i) {
    if (i < 0)
      return false;

    Queryable q = list.getCurrentList().get(i);
    return q instanceof Header;
  }
}
