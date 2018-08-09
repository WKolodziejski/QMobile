package com.qacademico.qacademico.Fragment.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Adapter.ViewPagerAdapter;
import com.qacademico.qacademico.Fragment.BoletimFragment;
import com.qacademico.qacademico.Fragment.DiariosFragment;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.WebView.SingletonWebView;

import java.util.Objects;

public class NotasFragment extends Fragment implements ViewPager.OnPageChangeListener {
    SingletonWebView webView = SingletonWebView.getInstance();
    ViewPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);

        adapter.addFragment(new DiariosFragment(), getString(R.string.title_diarios));
        adapter.addFragment(new BoletimFragment(), getString(R.string.title_boletim));

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(this);

        ((MainActivity)getActivity()).tabLayout.setupWithViewPager(viewPager);
        ((MainActivity)getActivity()).tabLayout.setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).hideCalendar();

        ((MainActivity) Objects.requireNonNull(getActivity())).showExpandBtn();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter = new ViewPagerAdapter(getChildFragmentManager());
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        if (i == 0) {
            ((MainActivity) Objects.requireNonNull(getActivity())).showExpandBtn();
        } else {
            ((MainActivity) Objects.requireNonNull(getActivity())).hideExpandBtn();
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
