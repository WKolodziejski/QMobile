package com.tinf.qmobile.fragment.matter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tinf.qmobile.R;
import com.tinf.qmobile.fragment.MaterialsFragment;
import com.tinf.qmobile.fragment.ScheduleFragment;

public class TabsAdapter extends FragmentPagerAdapter {
    private Bundle bundle;
    private Context context;

    public TabsAdapter(Context context, @NonNull FragmentManager fm, Bundle bundle) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.bundle = bundle;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0: fragment = new GradesFragment();
            break;

            case 1: fragment = new ScheduleFragment();
            break;

            case 2: fragment = new MaterialsFragment();
            break;

            case 3: fragment = new ClassFragment();
            break;
        }

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Resources resources = context.getResources();

        switch (position) {
            case 0: return resources.getString(R.string.title_notas);

            case 1: return resources.getString(R.string.title_horario);

            case 2: return resources.getString(R.string.title_materiais);

            case 3: return resources.getString(R.string.title_class);
        }
        return "";
    }

}
