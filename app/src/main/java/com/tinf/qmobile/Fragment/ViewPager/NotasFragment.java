package com.tinf.qmobile.Fragment.ViewPager;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.Adapter.ViewPagerAdapter;
import com.tinf.qmobile.Fragment.BoletimFragment;
import com.tinf.qmobile.Fragment.DiariosFragment;
import com.tinf.qmobile.Interfaces.Fragments.OnUpdate;
import com.tinf.qmobile.R;
import com.tinf.qmobile.WebView.SingletonWebView;
import java.util.Objects;

import static com.tinf.qmobile.Utilities.Utils.PG_BOLETIM;
import static com.tinf.qmobile.Utilities.Utils.PG_DIARIOS;
import static com.tinf.qmobile.Utilities.Utils.UPDATE_REQUEST;
import static com.tinf.qmobile.Utilities.Utils.URL;

public class NotasFragment extends Fragment implements ViewPager.OnPageChangeListener, OnUpdate {
    private SingletonWebView webView = SingletonWebView.getInstance();
    private int currentFragment = 0;
    private ViewPager viewPager;
    private OnTopScrollRequested onTopScrollRequestedB, onTopScrollRequestedD;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_viewpager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showNotas(view);
    }

    private void showNotas(View view) {

        ((MainActivity) getActivity()).setTitle(webView.data_year[webView.year_position]);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        adapter.addFragment(new DiariosFragment(), getString(R.string.title_diarios));
        adapter.addFragment(new BoletimFragment(), getString(R.string.title_boletim));

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
    public void onUpdate(String url_p) {
        if (url_p.equals(URL + PG_DIARIOS) || url_p.equals(URL + PG_BOLETIM) || url_p.equals(UPDATE_REQUEST)) {
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
        //((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.addOnPageChangeListener(this);
        //((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
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
