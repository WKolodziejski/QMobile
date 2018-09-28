package com.tinf.qacademico.Fragment.ViewPager;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import com.tinf.qacademico.Activity.MainActivity;
import com.tinf.qacademico.Adapter.ViewPagerAdapter;
import com.tinf.qacademico.Fragment.BoletimFragment;
import com.tinf.qacademico.Fragment.DiariosFragment;
import com.tinf.qacademico.R;
import com.tinf.qacademico.WebView.SingletonWebView;

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
            ((MainActivity) Objects.requireNonNull(getActivity())).showExpandBtn();
        } else {
            ((MainActivity) Objects.requireNonNull(getActivity())).hideExpandBtn();
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
