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
import android.widget.TextView;

import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Adapter.AdapterDiarios;
import com.qacademico.qacademico.Class.Diarios;
import com.qacademico.qacademico.R;
import com.sun.mail.iap.Argument;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import static com.qacademico.qacademico.Activity.MainActivity.pg_diarios;
import static com.qacademico.qacademico.Activity.MainActivity.url;

public class DiariosFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_diarios, container, false);

        List<Diarios> diarios = (List<Diarios>) getArguments().getSerializable("Diarios");

        if (diarios != null) {
            RecyclerView recyclerViewDiarios = (RecyclerView) view.findViewById(R.id.recycler_diarios);
            RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

            AdapterDiarios adapter = new AdapterDiarios(diarios, getActivity());

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
