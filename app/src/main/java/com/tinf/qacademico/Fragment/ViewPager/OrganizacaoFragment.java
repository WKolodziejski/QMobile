package com.tinf.qacademico.Fragment.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinf.qacademico.Activity.MainActivity;
import com.tinf.qacademico.Adapter.ViewPagerAdapter;
import com.tinf.qacademico.Fragment.BoletimFragment;
import com.tinf.qacademico.Fragment.CalendarioFragment;
import com.tinf.qacademico.Fragment.DiariosFragment;
import com.tinf.qacademico.Fragment.GraficosFragment;
import com.tinf.qacademico.R;

import java.util.Objects;

public class OrganizacaoFragment extends Fragment implements ViewPager.OnPageChangeListener {
    ViewPagerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);

        adapter.addFragment(new CalendarioFragment(), getString(R.string.title_calendario));
        adapter.addFragment(new GraficosFragment(), getString(R.string.title_graficos));

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(this);

        ((MainActivity)getActivity()).setupTabLayoutWithViewPager(viewPager);

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
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
