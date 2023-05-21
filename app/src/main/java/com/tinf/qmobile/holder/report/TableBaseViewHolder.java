package com.tinf.qmobile.holder.report;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.tinf.qmobile.model.matter.Matter;

public abstract class TableBaseViewHolder extends AbstractViewHolder {

  public TableBaseViewHolder(
      @NonNull
      View itemView) {
    super(itemView);
  }

  public abstract void bind(Context context, Matter matter, String cell);

}
