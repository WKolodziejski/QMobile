package com.tinf.qacademico.Fragment;

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
import com.tinf.qacademico.Activity.MainActivity;
import com.tinf.qacademico.Adapter.Diarios.DiariosListAdapter;
import com.tinf.qacademico.Class.Materias.Materia;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Data;
import com.tinf.qacademico.WebView.SingletonWebView;
import java.util.List;
import java.util.Objects;

import io.objectbox.BoxStore;

public class DiariosFragment extends Fragment implements MainActivity.OnPageUpdated {
    DiariosListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) Objects.requireNonNull(getActivity())).setOnPageUpdateListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diarios, container, false);

        setDiarios(view);

        return view;
    }

    private void setDiarios(View view) {

        RecyclerView recyclerViewDiarios = (RecyclerView) view.findViewById(R.id.recycler_diarios);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        adapter = new DiariosListAdapter(Data.loadMaterias(getContext()), getActivity());

        recyclerViewDiarios.setAdapter(adapter);
        recyclerViewDiarios.setLayoutManager(layout);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewDiarios.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerViewDiarios.addItemDecoration(dividerItemDecoration);

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

        ((MainActivity) Objects.requireNonNull(getActivity())).fab_expand.setOnClickListener(v -> {
            adapter.toggleAll();
        });
    }

    private BoxStore getBox() {
        return ((MainActivity) getActivity()).getBox();
    }

    @Override
    public void onPageUpdate(List<?> list) {

    }
}
