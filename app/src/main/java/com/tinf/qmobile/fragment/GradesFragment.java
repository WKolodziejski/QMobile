package com.tinf.qmobile.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayoutMediator;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.tab.GradesTabsAdapter;
import com.tinf.qmobile.databinding.FragmentGradesBinding;

public class GradesFragment extends BaseFragment {

  private FragmentGradesBinding binding;

  @Override
  public View onCreateView(
      @NonNull
      LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_grades, container, false);
    binding = FragmentGradesBinding.bind(view);
    return view;
  }

  @Override
  public void onViewCreated(
      @NonNull
      View view,
      @Nullable
      @org.jetbrains.annotations.Nullable
      Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    binding.pager.setAdapter(new GradesTabsAdapter(getChildFragmentManager(), getLifecycle()));

    new TabLayoutMediator(binding.tab, binding.pager, (tab, position) -> {
      Resources resources = getContext().getResources();
      switch (position) {
        case 0:
          tab.setText(resources.getString(R.string.title_diarios));
          break;

        case 1:
          tab.setText(resources.getString(R.string.title_boletim));
          break;
      }
    }).attach();

    binding.pager.setCurrentItem(0);
  }

  @Override
  protected void onAddListeners() {

  }

  @Override
  protected void onRemoveListeners() {

  }

  @Override
  protected void onScrollRequest() {
    ((BaseFragment) getChildFragmentManager().getFragments()
                                              .get(0)).requestScroll();
  }
}
