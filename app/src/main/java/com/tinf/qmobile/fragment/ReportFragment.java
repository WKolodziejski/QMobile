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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.adapter.ReportAdapter;
import com.tinf.qmobile.databinding.FragmentReportBinding;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.Design;

public class ReportFragment extends Fragment implements OnUpdate {
    private FragmentReportBinding binding;
    private SwipeRefreshLayout refresh;

    public void setParams(SwipeRefreshLayout refresh) {
        this.refresh = refresh;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        binding = FragmentReportBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.table.setShowHorizontalSeparators(false);
        binding.table.setShowVerticalSeparators(false);
        binding.table.getColumnHeaderRecyclerView().removeItemDecorationAt(0);
        binding.table.getRowHeaderRecyclerView().removeItemDecorationAt(0);
        binding.table.getCellRecyclerView().removeItemDecorationAt(0);
        binding.table.setAdapter(new ReportAdapter(getContext(), binding.table, binding.empty));
        binding.table.getCellRecyclerView().addOnScrollListener(Design.getRefreshBehavior(refresh));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.grades, menu);
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_grades).setIcon(R.drawable.ic_list);
    }

    @Override
    public void onScrollRequest() {
        binding.table.scrollToRowPosition(0);
        binding.table.scrollToColumnPosition(0);
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
