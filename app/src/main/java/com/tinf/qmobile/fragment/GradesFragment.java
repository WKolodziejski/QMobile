package com.tinf.qmobile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.tab.GradesTabsAdapter;
import com.tinf.qmobile.databinding.FragmentGradesBinding;

public class GradesFragment extends BaseFragment {

  private FragmentGradesBinding binding;

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
    binding.pager.setUserInputEnabled(false);
    binding.pager.setCurrentItem(0);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.grades, menu);
    super.onCreateOptionsMenu(menu, inflater);
    menu.findItem(R.id.action_grades).setIcon(R.drawable.ic_column);
  }

  @Override
  public boolean onOptionsItemSelected(
      @NonNull
      MenuItem item) {

    if (item.getItemId() == R.id.action_grades) {
      if (binding.pager.getCurrentItem() == 0) {
        binding.pager.setCurrentItem(1);
        item.setIcon(R.drawable.ic_list);
        return true;
      }

      if (binding.pager.getCurrentItem() == 1) {
        binding.pager.setCurrentItem(0);
        item.setIcon(R.drawable.ic_column);
        return true;
      }
    }

    return super.onOptionsItemSelected(item);
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
