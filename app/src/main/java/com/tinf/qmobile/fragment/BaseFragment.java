package com.tinf.qmobile.fragment;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.tinf.qmobile.network.Client;

public abstract class BaseFragment extends Fragment {
    protected MaterialToolbar toolbar;
    protected NestedScrollView scroll;
    protected SwipeRefreshLayout refresh;

    protected abstract void onAddListeners();
    protected abstract void onRemoveListeners();

    public void setParams(MaterialToolbar toolbar, NestedScrollView scroll, SwipeRefreshLayout refresh) {
        this.toolbar = toolbar;
        this.scroll = scroll;
        this.refresh = refresh;
    }

    @Override
    public void onStart() {
        super.onStart();
        onAddListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        onAddListeners();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onAddListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
        onRemoveListeners();
    }

    @Override
    public void onStop() {
        super.onStop();
        onRemoveListeners();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onRemoveListeners();
    }

}
