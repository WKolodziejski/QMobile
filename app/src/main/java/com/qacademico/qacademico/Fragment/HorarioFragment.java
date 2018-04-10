package com.qacademico.qacademico.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Adapter.AdapterHorario;
import com.qacademico.qacademico.Class.Diarios;
import com.qacademico.qacademico.Class.Horario;
import com.qacademico.qacademico.R;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import static com.qacademico.qacademico.Activity.MainActivity.pg_horario;
import static com.qacademico.qacademico.Activity.MainActivity.url;

public class HorarioFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_horario, container, false);

        List<Horario> horario = (List<Horario>) getArguments().getSerializable("Horario");

        if (horario != null) {

            RecyclerView recyclerViewHorario = (RecyclerView) view.findViewById(R.id.recycler_horario);
            RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

            AdapterHorario adapter = new AdapterHorario(horario, getActivity());

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
