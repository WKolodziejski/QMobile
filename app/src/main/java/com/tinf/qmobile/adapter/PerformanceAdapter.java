package com.tinf.qmobile.adapter;

import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.MATTER;
import static com.tinf.qmobile.network.Client.pos;

import android.content.Context;
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
import com.tinf.qmobile.holder.performance.PerformanceEmptyViewHolder;
import com.tinf.qmobile.holder.performance.PerformanceHeaderViewHolder;
import com.tinf.qmobile.holder.performance.PerformanceViewHolder;
import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.utility.UserUtils;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

public class PerformanceAdapter extends RecyclerView.Adapter<PerformanceViewHolder> {
  private final Context context;
  private final AsyncListDiffer<Queryable> list;
  private final DataSubscription sub1;
  private final Handler handler;

  public PerformanceAdapter(Context context) {
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

    updateList();

    DataObserver observer = data -> updateList();

    sub1 = DataBase.get().getBoxStore().subscribe(Matter.class)
                   .onlyChanges()
                   .onError(Throwable::printStackTrace)
                   .observer(observer);
  }

  private void updateList() {
    DataBase.get().execute(() -> {
      List<Queryable> list = getList();
      handler.post(() -> this.list.submitList(list));
    });
  }

  private List<Queryable> getList() {
    List<Matter> matters = DataBase.get().getBoxStore()
                                   .boxFor(Matter.class)
                                   .query()
                                   .order(Matter_.title_)
                                   .equal(Matter_.year_, UserUtils.getYear(pos))
                                   .and()
                                   .equal(Matter_.period_, UserUtils.getPeriod(pos))
                                   .build()
                                   .find();

    List<Queryable> list = new ArrayList<>(matters);

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
  public PerformanceViewHolder onCreateViewHolder(
      @NonNull
      ViewGroup parent, int viewType) {
    switch (viewType) {
      case MATTER:
        return new PerformanceHeaderViewHolder(LayoutInflater.from(context)
                                                             .inflate(R.layout.chart_header, parent,
                                                                      false));

      case EMPTY:
        return new PerformanceEmptyViewHolder(LayoutInflater.from(context)
                                                            .inflate(R.layout.chart_empty, parent,
                                                                     false));
    }

    return null;
  }

  @Override
  public void onBindViewHolder(
      @NonNull
      PerformanceViewHolder holder, int i) {
    holder.bind(context, list.getCurrentList().get(i));
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
  }

}
