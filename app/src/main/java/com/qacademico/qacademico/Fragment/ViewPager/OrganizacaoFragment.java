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

import com.qacademico.qacademico.Adapter.ViewPagerAdapter;
import com.qacademico.qacademico.Fragment.BoletimFragment;
import com.qacademico.qacademico.Fragment.CalendarioFragment;
import com.qacademico.qacademico.Fragment.DiariosFragment;
import com.qacademico.qacademico.Fragment.GraficosFragment;
import com.qacademico.qacademico.R;

public class OrganizacaoFragment extends Fragment {
    ViewPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);

        adapter.addFragment(new CalendarioFragment(), getString(R.string.title_calendario));
        adapter.addFragment(new GraficosFragment(), getString(R.string.title_graficos));

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter = new ViewPagerAdapter(getChildFragmentManager());
    }
}
