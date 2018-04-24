package com.qacademico.qacademico.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Adapter.HorarioAdapter;
import com.qacademico.qacademico.Class.Horario;
import com.qacademico.qacademico.R;

import java.util.List;


public class HorarioFragment extends Fragment {
    List<Horario> horario;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            horario = (List<Horario>) getArguments().getSerializable("Horario");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horario, container, false);

        if (horario != null) {

            RecyclerView recyclerViewHorario = (RecyclerView) view.findViewById(R.id.recycler_horario);
            RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

            HorarioAdapter adapter = new HorarioAdapter(horario, getActivity());

            recyclerViewHorario.setAdapter(adapter);
            recyclerViewHorario.setLayoutManager(layout);

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

        }
        return view;
    }
}
