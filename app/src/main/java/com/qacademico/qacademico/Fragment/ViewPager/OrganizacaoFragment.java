package com.qacademico.qacademico.Fragment.ViewPager;

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

import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Adapter.ViewPagerAdapter;
import com.qacademico.qacademico.Fragment.BoletimFragment;
import com.qacademico.qacademico.Fragment.CalendarioFragment;
import com.qacademico.qacademico.Fragment.DiariosFragment;
import com.qacademico.qacademico.Fragment.GraficosFragment;
import com.qacademico.qacademico.R;

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
        if (i == 0) {
            ((MainActivity)getActivity()).showCalendar();
        } else {
            ((MainActivity)getActivity()).hideCalendar();
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
