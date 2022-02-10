package com.tinf.qmobile.fragment.matter;

import static com.tinf.qmobile.model.ViewType.CLASS;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MATERIAL;
import static com.tinf.qmobile.model.ViewType.SCHEDULE;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.tinf.qmobile.fragment.MaterialsFragment;
import com.tinf.qmobile.fragment.ScheduleFragment;

public class TabsAdapter extends FragmentStateAdapter {
    private final Bundle bundle;

    public TabsAdapter(FragmentManager fm, Lifecycle lifecycle, Bundle bundle) {
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
                fragment = new MaterialsFragment();
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
