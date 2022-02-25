package com.tinf.qmobile.fragment;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.tinf.qmobile.network.Client;

public abstract class BaseFragment extends Fragment implements OnUpdate {
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
    public void onScrollRequest() {
        //Override if the behaviour is desired
    }

    @Override
    public void onDateChanged() {
        //Override if the behaviour is desired
    }

    @Override
    public void onStart() {
        super.onStart();
        onAddListeners();
        Client.get().addOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        onAddListeners();
        Client.get().addOnUpdateListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onAddListeners();
        Client.get().addOnUpdateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        onRemoveListeners();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        onRemoveListeners();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onRemoveListeners();
        Client.get().removeOnUpdateListener(this);
    }

}
