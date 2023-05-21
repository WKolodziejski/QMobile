package com.tinf.qmobile.holder.performance;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.model.Queryable;

public abstract class PerformanceViewHolder<T extends Queryable> extends RecyclerView.ViewHolder {

  public PerformanceViewHolder(
      @NonNull
      View itemView) {
    super(itemView);
  }

  public abstract void bind(Context context, T t);
}
