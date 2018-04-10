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
import com.qacademico.qacademico.Adapter.AdapterBoletim;
import com.qacademico.qacademico.Class.Boletim;
import com.qacademico.qacademico.Class.Diarios;
import com.qacademico.qacademico.R;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import static com.qacademico.qacademico.Activity.MainActivity.pg_boletim;
import static com.qacademico.qacademico.Activity.MainActivity.url;


public class BoletimFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_boletim, container, false);

        List<Boletim> boletim = (List<Boletim>) getArguments().getSerializable("Boletim");

        if (boletim != null) {

            RecyclerView recyclerViewBoletim = (RecyclerView) view.findViewById(R.id.recycler_boletim);
            RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

            AdapterBoletim adapter = new AdapterBoletim(boletim, getActivity());

            recyclerViewBoletim.setAdapter(adapter);
            recyclerViewBoletim.setLayoutManager(layout);
            recyclerViewBoletim.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

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
