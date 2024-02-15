package com.tinf.qmobile.adapter;

import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.FOOTER_JOURNAL;
import static com.tinf.qmobile.model.ViewType.FOOTER_PERIOD;
import static com.tinf.qmobile.model.ViewType.HEADER;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.JOURNAL_EMPTY;
import static com.tinf.qmobile.model.ViewType.MATTER;
import static com.tinf.qmobile.model.ViewType.PERIOD;

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
import com.tinf.qmobile.holder.journal.JournalBaseViewHolder;
import com.tinf.qmobile.holder.journal.JournalEmptyViewHolder;
import com.tinf.qmobile.holder.journal.JournalFooterViewHolder;
import com.tinf.qmobile.holder.journal.JournalHeaderColorViewHolder;
import com.tinf.qmobile.holder.journal.JournalHeaderViewHolder;
import com.tinf.qmobile.holder.journal.JournalItemViewHolder;
import com.tinf.qmobile.holder.journal.PeriodFooterViewHolder;
import com.tinf.qmobile.holder.journal.PeriodHeaderViewHolder;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.Header;

import java.util.List;

public class JournalsAdapter extends RecyclerView.Adapter<JournalBaseViewHolder>
    implements KmStickyListener, OnData<Queryable> {
  private final AsyncListDiffer<Queryable> list;
  private final Context context;
  private final OnList<Queryable> onList;
  private int currentHeader;

  public JournalsAdapter(Context context, OnList<Queryable> onList) {
    this.context = context;
    this.onList = onList;
    this.list = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<Queryable>() {
      @Override
      public boolean areItemsTheSame(
          @NonNull
          Queryable oldItem,
          @NonNull
          Queryable newItem) {
        return oldItem.getId() == newItem.getId() && oldItem.getItemType() == newItem.getItemType();
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

    onUpdate(DataBase.get()
                     .getJournalsDataProvider()
                     .getList());
  }

  @NonNull
  @Override
  public JournalBaseViewHolder onCreateViewHolder(
      @NonNull
      ViewGroup parent,
      int viewType) {
    switch (viewType) {
      case MATTER:
        return new JournalHeaderViewHolder(LayoutInflater.from(context)
                                                         .inflate(R.layout.journal_header, parent,
                                                                  false));

      case JOURNAL:
        return new JournalItemViewHolder(LayoutInflater.from(context)
                                                       .inflate(R.layout.journal_item, parent, false));

      case FOOTER_JOURNAL:
        return new JournalFooterViewHolder(LayoutInflater.from(context)
                                                         .inflate(R.layout.journal_footer, parent,
                                                                  false));

      case FOOTER_PERIOD:
        return new PeriodFooterViewHolder(LayoutInflater.from(context)
                                                        .inflate(R.layout.period_footer, parent,
                                                                 false));

      case PERIOD:
        return new PeriodHeaderViewHolder(LayoutInflater.from(context)
                                                        .inflate(R.layout.period_header, parent,
                                                                 false));

      case JOURNAL_EMPTY:
        return new JournalEmptyViewHolder(LayoutInflater.from(context)
                                                        .inflate(R.layout.journal_item_empty,
                                                                 parent, false));

      case HEADER:
        return new JournalHeaderColorViewHolder(LayoutInflater.from(context)
                                                              .inflate(R.layout.header_empty,
                                                                       parent, false));

      case EMPTY:
        return new JournalEmptyViewHolder(LayoutInflater.from(context)
                                                        .inflate(R.layout.journal_empty, parent,
                                                                 false));
    }

    return null;
  }

  @Override
  public int getItemViewType(int i) {
    return list.getCurrentList().get(i).getItemType();
  }

  @Override
  public void onBindViewHolder(
      @NonNull
      JournalBaseViewHolder holder, int i) {
    holder.bind(context, list.getCurrentList().get(i), true, i == currentHeader);
  }

  @Override
  public int getItemCount() {
    return list.getCurrentList().size();
  }

  @Override
  public void onUpdate(List<Queryable> list) {
    this.list.submitList(DataBase.get().getJournalsDataProvider().getList());
    onList.onUpdate(list);
  }

  @Override
  public void onDetachedFromRecyclerView(
      @NonNull
      RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    DataBase.get().getJournalsDataProvider().removeOnDataListener(this);
  }

  @Override
  public void onAttachedToRecyclerView(
      @NonNull
      RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    DataBase.get().getJournalsDataProvider().addOnDataListener(this);
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
