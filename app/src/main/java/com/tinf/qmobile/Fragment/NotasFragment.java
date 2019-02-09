package com.tinf.qmobile.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.Adapter.ViewPagerAdapter;
import com.tinf.qmobile.Fragment.BoletimFragment;
import com.tinf.qmobile.Fragment.DiariosFragment;
import com.tinf.qmobile.Interfaces.OnUpdate;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.User;

import java.util.Objects;

import static com.tinf.qmobile.Utilities.Utils.UPDATE_REQUEST;

public class NotasFragment extends Fragment implements ViewPager.OnPageChangeListener, OnUpdate {
    private static String TAG = "NotasFragment";
    private int currentFragment = 0;
    private ViewPager viewPager;
    private OnTopScrollRequested onTopScrollRequestedB, onTopScrollRequestedD;
    private ViewPagerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "New instace created");

        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new DiariosFragment(), getString(R.string.title_diarios));
        adapter.addFragment(new BoletimFragment(), getString(R.string.title_boletim));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_viewpager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v(TAG, "View created");
        showNotas(view);
    }

    private void showNotas(View view) {

        ((MainActivity) getActivity()).setTitle(User.getYears()[0]);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentFragment);

        ((MainActivity)Objects.requireNonNull(getActivity())).setupTabLayoutWithViewPager(viewPager);
    }

    @Override
    public void onPageSelected(int i) {
        currentFragment = i;
        if (i == 0) {
            ((MainActivity) Objects.requireNonNull(getActivity())).showExpandBtn();
        } else {
            ((MainActivity) Objects.requireNonNull(getActivity())).hideExpandBtn();
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {}

    @Override
    public void onPageScrolled(int i, float v, int i1) {}

    public void setOnTopScrollRequestedBListener(OnTopScrollRequested onTopScrollRequested) {
        this.onTopScrollRequestedB = onTopScrollRequested;
    }

    public void setOnTopScrollRequestedDListener(OnTopScrollRequested onTopScrollRequested) {
        this.onTopScrollRequestedD = onTopScrollRequested;
    }

    @Override
    public void onUpdate(int pg) {
        if (pg == UPDATE_REQUEST) {
            onCreate(null);
            showNotas(getView());
        }
    }

    @Override
    public void requestScroll() {
        switch (currentFragment) {
            case 0: onTopScrollRequestedD.onTopScrollRequested();
                break;
            case 1: onTopScrollRequestedB.onTopScrollRequested();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        viewPager.addOnPageChangeListener(this);
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.addOnPageChangeListener(this);
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        viewPager.addOnPageChangeListener(null);
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        viewPager.addOnPageChangeListener(this);
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(null);
    }

    public interface OnTopScrollRequested {
        void onTopScrollRequested();
    }
}
