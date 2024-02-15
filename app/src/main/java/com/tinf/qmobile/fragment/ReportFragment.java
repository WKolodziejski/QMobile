package com.tinf.qmobile.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.ReportAdapter;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.database.OnData;
import com.tinf.qmobile.databinding.FragmentReportBinding;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.utility.DesignUtils;

import java.util.List;

public class ReportFragment extends BaseFragment implements OnData<Queryable> {
  private FragmentReportBinding binding;

  @Override
  public View onCreateView(
      @NonNull
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_report, container, false);
    binding = FragmentReportBinding.bind(view);
    return view;
  }

  @Override
  public void onViewCreated(
      @NonNull
      View view,
      @Nullable
      Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    binding.table.setShowHorizontalSeparators(false);
    binding.table.setShowVerticalSeparators(false);
    binding.table.getColumnHeaderRecyclerView().removeItemDecorationAt(0);
    binding.table.getRowHeaderRecyclerView().removeItemDecorationAt(0);
    binding.table.getCellRecyclerView().removeItemDecorationAt(0);
    binding.table.setAdapter(new ReportAdapter(getContext(), binding.table, binding.empty));
    binding.table.getCellRecyclerView().addOnScrollListener(DesignUtils.getRefreshBehavior(refresh));

    new Handler(Looper.getMainLooper()).postDelayed(() -> DesignUtils.syncToolbar(toolbar, canExpand()), 10);
  }

  private boolean canExpand() {
    return !DataBase.get().getJournalsDataProvider().getList().isEmpty();
  }

  @Override
  protected void onAddListeners() {
    DataBase.get().getJournalsDataProvider().addOnDataListener(this);
    DesignUtils.syncToolbar(toolbar, canExpand());
  }

  @Override
  protected void onRemoveListeners() {
    DataBase.get().getJournalsDataProvider().removeOnDataListener(this);
  }

  @Override
  protected void onScrollRequest() {
    binding.scroll.smoothScrollTo(0, 0);
  }

  @Override
  public void onUpdate(List<Queryable> list) {
    DesignUtils.syncToolbar(toolbar, !list.isEmpty());
  }
}
