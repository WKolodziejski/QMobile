package com.qacademico.qacademico.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Adapter.ExpandableListAdapter;
import com.qacademico.qacademico.Class.ExpandableList;
import com.qacademico.qacademico.Class.Materiais;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;

import java.util.List;
import java.util.Objects;

public class MateriaisFragment extends Fragment implements MainActivity.OnPageUpdated {
    public List<ExpandableList> materiaisList;
    ExpandableListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) Objects.requireNonNull(getActivity())).setOnPageUpdateListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materiais, container, false);

        if (materiaisList != null) {
            showMateriais(view);
        }

        return view;
    }

    private void showMateriais(View view) {
        RecyclerView recyclerViewMateriais = (RecyclerView) view.findViewById(R.id.recycler_materiais);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        adapter = new ExpandableListAdapter(materiaisList, getActivity(), Utils.MATERIAIS);

        recyclerViewMateriais.setAdapter(adapter);
        recyclerViewMateriais.setLayoutManager(layout);
        //recyclerViewDiarios.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewMateriais.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerViewMateriais.addItemDecoration(dividerItemDecoration);

        adapter.setOnExpandListener(position -> {
            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(Objects.requireNonNull(getActivity())) {
                @Override
                protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_ANY;
                }
            };
            if (position != 0) {
                smoothScroller.setTargetPosition(position);
                layout.startSmoothScroll(smoothScroller);
            }
        });
    }

    @Override
    public void onPageUpdate(List<?> list) {
        materiaisList = (List<ExpandableList>) list;
        showMateriais(getView());
    }
}
