package com.tinf.qmobile.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kodmap.library.kmrecyclerviewstickyheader.KmHeaderItemDecoration;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.JournalsAdapter;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.database.OnData;
import com.tinf.qmobile.databinding.FragmentJournalsBinding;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.utility.DesignUtils;
import com.tinf.qmobile.widget.divider.CustomItemDivider;

import java.util.List;

public class JournalsFragment extends BaseFragment implements OnData<Queryable> {
  private FragmentJournalsBinding binding;

  @Override
  public View onCreateView(
      @NonNull
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_journals, container, false);
    binding = FragmentJournalsBinding.bind(view);
    return view;
  }

  @Override
  public void onViewCreated(
      @NonNull
      View view,
      @Nullable
      Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    JournalsAdapter adapter = new JournalsAdapter(requireContext(), this::onUpdate);

    binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
    binding.recycler.addItemDecoration(new CustomItemDivider(requireContext()));
    binding.recycler.setItemAnimator(null);
    binding.recycler.setAdapter(adapter);
    binding.recycler.addItemDecoration(new KmHeaderItemDecoration(adapter));
    binding.recycler.addOnScrollListener(DesignUtils.getRefreshBehavior(refresh));

    new Handler(Looper.getMainLooper()).postDelayed(
        () -> DesignUtils.syncToolbar(toolbar, canExpand()), 10);
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
    binding.recycler.smoothScrollToPosition(0);
  }

  @Override
  public void onUpdate(List<Queryable> list) {
    DesignUtils.syncToolbar(toolbar, !list.isEmpty());
  }

}
