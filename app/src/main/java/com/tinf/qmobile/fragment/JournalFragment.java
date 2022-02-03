package com.tinf.qmobile.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.JournalAdapter;
import com.tinf.qmobile.databinding.FragmentJournalBinding;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.Design;
import com.tinf.qmobile.widget.divider.JournalItemDivider;

public class JournalFragment extends Fragment implements OnUpdate {
    private FragmentJournalBinding binding;
    private MaterialToolbar toolbar;
    private NestedScrollView scroll;
    private SwipeRefreshLayout refresh;

    public void setParams(MaterialToolbar toolbar, NestedScrollView scroll, SwipeRefreshLayout refresh) {
        this.toolbar = toolbar;
        this.scroll = scroll;
        this.refresh = refresh;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal, container, false);
        binding = FragmentJournalBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recycler.setHasFixedSize(true);
        binding.recycler.setItemViewCacheSize(20);
        binding.recycler.setDrawingCacheEnabled(true);
        binding.recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycler.addItemDecoration(new JournalItemDivider(getContext(), 52));
        binding.recycler.setAdapter(new JournalAdapter(getContext(), null,
                canExpand -> Design.syncToolbar(toolbar, Design.canScroll(scroll) && canExpand)));
        binding.recycler.addOnScrollListener(Design.getRefreshBehavior(refresh));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.grades, menu);
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_grades).setIcon(R.drawable.ic_column);
    }

    @Override
    public void onScrollRequest() {
        if (binding.recycler != null) {
            binding.recycler.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onDateChanged() {

    }

    @Override
    public void onStart() {
        super.onStart();
        Client.get().addOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Client.get().addOnUpdateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Client.get().addOnUpdateListener(this);
    }

}
