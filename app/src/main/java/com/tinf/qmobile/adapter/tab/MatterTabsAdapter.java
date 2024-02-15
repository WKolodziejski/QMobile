package com.tinf.qmobile.adapter.tab;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.tinf.qmobile.fragment.ScheduleFragment;
import com.tinf.qmobile.fragment.matter.ClassFragment;
import com.tinf.qmobile.fragment.matter.GradesFragment;
import com.tinf.qmobile.fragment.matter.InfoFragment;
import com.tinf.qmobile.fragment.matter.SuppliesFragment;

public class MatterTabsAdapter extends FragmentStateAdapter {
  private final Bundle bundle;

  public MatterTabsAdapter(FragmentManager fm, Lifecycle lifecycle, Bundle bundle) {
    super(fm, lifecycle);
    this.bundle = bundle;
  }

  @NonNull
  @Override
  public Fragment createFragment(int position) {
    Fragment fragment = null;

    switch (position) {
      case 0:
        fragment = new InfoFragment();
        break;

      case 1:
        fragment = new GradesFragment();
        break;

      case 2:
        fragment = new ScheduleFragment();
        break;

      case 3:
        fragment = new SuppliesFragment();
        break;

      case 4:
        fragment = new ClassFragment();
        break;
    }

    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public int getItemCount() {
    return 5;
  }

}
