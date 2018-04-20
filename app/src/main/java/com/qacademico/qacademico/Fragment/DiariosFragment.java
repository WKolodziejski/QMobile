package com.qacademico.qacademico.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Adapter.DiariosAdapter;
import com.qacademico.qacademico.Class.Diarios;
import com.qacademico.qacademico.R;
import java.util.List;

public class DiariosFragment extends Fragment {
    List<Diarios> diarios;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            diarios = (List<Diarios>) getArguments().getSerializable("Diarios");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diarios, container, false);

        if (diarios != null) {
            RecyclerView recyclerViewDiarios = (RecyclerView) view.findViewById(R.id.recycler_diarios);
            RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

            DiariosAdapter adapter = new DiariosAdapter(diarios, getActivity());

            recyclerViewDiarios.setAdapter(adapter);
            recyclerViewDiarios.setLayoutManager(layout);
            recyclerViewDiarios.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

            adapter.setOnExpandListener(position -> {
                RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getActivity()) {
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

            ((MainActivity) getActivity()).fab_expand.setOnClickListener(v -> {
                adapter.toggleAll();
                ((MainActivity) getActivity()).fab_isOpen = true;
                ((MainActivity) getActivity()).clickButtons(null);
            });

        } else {
            ((MainActivity) getActivity()).showErrorConnection();
        }
        return view;
    }
}
