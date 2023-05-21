package com.tinf.qmobile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.ReportAdapter;
import com.tinf.qmobile.databinding.FragmentReportBinding;
import com.tinf.qmobile.utility.Design;

public class ReportFragment extends Fragment {
  private FragmentReportBinding binding;
  private SwipeRefreshLayout refresh;

  public void setParams(SwipeRefreshLayout refresh) {
    this.refresh = refresh;
  }

  @Override
  public void onCreate(
      @Nullable
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

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
    binding.table.getCellRecyclerView().addOnScrollListener(Design.getRefreshBehavior(refresh));
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.grades, menu);
    super.onCreateOptionsMenu(menu, inflater);
    menu.findItem(R.id.action_grades).setIcon(R.drawable.ic_list);
  }

}
