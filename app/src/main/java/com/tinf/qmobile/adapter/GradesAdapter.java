package com.tinf.qmobile.adapter;

import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.FOOTERJOURNAL;
import static com.tinf.qmobile.model.ViewType.FOOTERPERIOD;
import static com.tinf.qmobile.model.ViewType.HEADER;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.JOURNALEMPTY;
import static com.tinf.qmobile.model.ViewType.MATTER;
import static com.tinf.qmobile.model.ViewType.PERIOD;

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
import com.tinf.qmobile.holder.journal.JournalBaseViewHolder;
import com.tinf.qmobile.holder.journal.JournalEmptyViewHolder;
import com.tinf.qmobile.holder.journal.JournalFooterViewHolder;
import com.tinf.qmobile.holder.journal.JournalHeaderColorViewHolder;
import com.tinf.qmobile.holder.journal.JournalHeaderViewHolder;
import com.tinf.qmobile.holder.journal.JournalViewHolder;
import com.tinf.qmobile.holder.journal.PeriodFooterViewHolder;
import com.tinf.qmobile.holder.journal.PeriodHeaderViewHolder;
import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.FooterPeriod;
import com.tinf.qmobile.model.journal.Header;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

public class GradesAdapter extends RecyclerView.Adapter<JournalBaseViewHolder> {
  private final Context context;
  private final AsyncListDiffer<Queryable> list;
  private final DataSubscription sub1;
  private final DataSubscription sub2;
  private final Handler handler;

  public GradesAdapter(Context context, Bundle bundle) {
    this.context = context;
    this.handler = new Handler(Looper.getMainLooper());
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

    updateList(bundle);

    DataObserver observer = data -> updateList(bundle);

    sub1 = DataBase.get().getBoxStore().subscribe(Matter.class)
                   .onlyChanges()
                   .onError(Throwable::printStackTrace)
                   .observer(observer);

    sub2 = DataBase.get().getBoxStore().subscribe(Journal.class)
                   .onlyChanges()
                   .onError(Throwable::printStackTrace)
                   .observer(observer);
  }

  private void updateList(Bundle bundle) {
    DataBase.get().execute(() -> {
      List<Queryable> list = getList(bundle);
      handler.post(() -> this.list.submitList(list));
    });
  }

  private List<Queryable> getList(Bundle bundle) {
    ArrayList<Queryable> list = new ArrayList<>();

    Matter matter = DataBase.get().getBoxStore()
                            .boxFor(Matter.class)
                            .get(bundle.getLong("ID"));

    for (Period period : matter.periods) {
      if (!period.journals.isEmpty()) {
        list.add(new Header(matter));
        list.add(period);
        list.addAll(period.journals);

        if (!period.isSub_())
          list.add(new FooterPeriod(period));
      }
    }

    if (list.isEmpty())
      list.add(new Empty());

    return list;
  }

  @NonNull
  @Override
  public JournalBaseViewHolder onCreateViewHolder(
      @NonNull
      ViewGroup parent, int viewType) {
    switch (viewType) {
      case MATTER:
        return new JournalHeaderViewHolder(LayoutInflater.from(context)
                                                         .inflate(R.layout.journal_header, parent,
                                                                  false));

      case JOURNAL:
        return new JournalViewHolder(LayoutInflater.from(context)
                                                   .inflate(R.layout.journal_item, parent, false));

      case FOOTERJOURNAL:
        return new JournalFooterViewHolder(LayoutInflater.from(context)
                                                         .inflate(R.layout.journal_footer, parent,
                                                                  false));

      case FOOTERPERIOD:
        return new PeriodFooterViewHolder(LayoutInflater.from(context)
                                                        .inflate(R.layout.period_footer, parent,
                                                                 false));

      case PERIOD:
        return new PeriodHeaderViewHolder(LayoutInflater.from(context)
                                                        .inflate(R.layout.period_header, parent,
                                                                 false));

      case HEADER:
        return new JournalHeaderColorViewHolder(LayoutInflater.from(context)
                                                              .inflate(R.layout.header_empty,
                                                                       parent, false));

      case JOURNALEMPTY:
        return new JournalEmptyViewHolder(LayoutInflater.from(context)
                                                        .inflate(R.layout.journal_item_empty,
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
    holder.bind(context, list.getCurrentList().get(i), false, false);
  }

  @Override
  public int getItemCount() {
    return list.getCurrentList().size();
  }

  @Override
  public void onDetachedFromRecyclerView(
      @NonNull
      RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    sub1.cancel();
    sub2.cancel();
  }

}
