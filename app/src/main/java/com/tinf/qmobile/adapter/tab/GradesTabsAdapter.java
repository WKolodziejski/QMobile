package com.tinf.qmobile.adapter.tab;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.tinf.qmobile.fragment.JournalsFragment;
import com.tinf.qmobile.fragment.ReportFragment;

public class GradesTabsAdapter extends FragmentStateAdapter {

  public GradesTabsAdapter(FragmentManager fm, Lifecycle lifecycle) {
    super(fm, lifecycle);
  }

  @NonNull
  @Override
  public Fragment createFragment(int position) {
    Fragment fragment = null;

    switch (position) {
      case 0:
        fragment = new JournalsFragment();
        break;

      case 1:
        fragment = new ReportFragment();
        break;
    }

    return fragment;
  }

  @Override
  public int getItemCount() {
    return 2;
  }

}
