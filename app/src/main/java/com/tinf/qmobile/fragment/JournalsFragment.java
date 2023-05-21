package com.tinf.qmobile.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.tinf.qmobile.utility.Design;
import com.tinf.qmobile.widget.divider.CustomlItemDivider;

import java.util.List;

public class JournalsFragment extends BaseFragment implements OnData<Queryable> {
  private FragmentJournalsBinding binding;

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

    JournalsAdapter adapter = new JournalsAdapter(getContext(), this::onUpdate);

    binding.recycler.setItemViewCacheSize(20);
    binding.recycler.setDrawingCacheEnabled(true);
    binding.recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.recycler.addItemDecoration(new CustomlItemDivider(getContext()));
    binding.recycler.setItemAnimator(null);
    binding.recycler.setAdapter(adapter);
    binding.recycler.addItemDecoration(new KmHeaderItemDecoration(adapter));
    binding.recycler.addOnScrollListener(Design.getRefreshBehavior(refresh));

    new Handler(Looper.getMainLooper()).postDelayed(() -> {
      Design.syncToolbar(toolbar, canExpand());
    }, 10);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.grades, menu);
    super.onCreateOptionsMenu(menu, inflater);
    menu.findItem(R.id.action_grades).setIcon(R.drawable.ic_column);
  }

  private boolean canExpand() {
    return !DataBase.get().getJournalsDataProvider().getList().isEmpty();
  }

  @Override
  protected void onAddListeners() {
    DataBase.get().getJournalsDataProvider().addOnDataListener(this);
    Design.syncToolbar(toolbar, canExpand());
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
    Design.syncToolbar(toolbar, !list.isEmpty());
  }

}
