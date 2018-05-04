package com.qacademico.qacademico.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.NumberPicker;

import com.alamkanak.weekview.WeekViewEvent;
import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Adapter.HorarioAdapter;
import com.qacademico.qacademico.Class.Horario;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;

import java.util.ArrayList;
import java.util.List;

import static com.qacademico.qacademico.Utilities.Utils.pg_horario;
import static com.qacademico.qacademico.Utilities.Utils.url;


public class HorarioFragment extends Fragment {
    SingletonWebView mainWebView = SingletonWebView.getInstance();
    List<Horario> horario;
    HorarioAdapter adapter;
    List<WeekViewEvent> week = new ArrayList<WeekViewEvent>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            horario = (List<Horario>) getArguments().getSerializable("Horario");
            for (int i = 0; i < horario.size(); i++) {
                week.get(i).setName(horario.get(0).getMateriasList().get(0).getMateria());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horario, container, false);

        setHorario(view);

        return view;
    }

    private void setHorario(View view) {

        if (horario != null) {

            RecyclerView recyclerViewHorario = (RecyclerView) view.findViewById(R.id.recycler_horario);
            RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

            adapter = new HorarioAdapter(horario, getActivity());

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

            setFABListener();

            ((MainActivity) getActivity()).fab_expand.setOnClickListener(v -> {
                adapter.toggleAll();
                ((MainActivity) getActivity()).fab_isOpen = true;
                ((MainActivity) getActivity()).clickButtons(null);
            });
        }
    }

    private void setFABListener(){
        if (mainWebView.pg_horario_loaded && mainWebView.data_horario != null) {

            ((MainActivity) getActivity()).fab_data.setOnClickListener(v -> {

                ((MainActivity) getActivity()).fab_data.setClickable(true);

                ((MainActivity) getActivity()).fab_isOpen = true;
                ((MainActivity) getActivity()).clickButtons(null);

                View theView = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);

                final NumberPicker year = (NumberPicker) theView.findViewById(R.id.year_picker);
                year.setMinValue(0);
                year.setMaxValue(mainWebView.data_horario.length - 1);
                year.setValue(mainWebView.data_position_horario);
                year.setDisplayedValues(mainWebView.data_horario);
                year.setWrapSelectorWheel(false);

                final NumberPicker periodo = (NumberPicker) theView.findViewById(R.id.periodo_picker);
                periodo.setMinValue(0);
                periodo.setMaxValue(mainWebView.periodo_horario.length - 1);
                periodo.setValue(mainWebView.periodo_position_horario);
                periodo.setDisplayedValues(mainWebView.periodo_horario);
                periodo.setWrapSelectorWheel(false);

                new AlertDialog.Builder(getActivity()).setView(theView)
                        .setCustomTitle(Utils.customAlertTitle(getActivity(), R.drawable.ic_date_range_black_24dp,
                                R.string.dialog_date_change, R.color.horario_dialog))
                        .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {

                            mainWebView.data_position_horario = year.getValue();
                            mainWebView.periodo_position_horario = periodo.getValue();


                            if (mainWebView.data_position_horario == Integer.parseInt(mainWebView.data_horario[0])) {
                                mainWebView.html.loadUrl(url + pg_horario);
                            } else {
                                mainWebView.html.loadUrl(url + pg_horario + "&COD_MATRICULA=-1&cmbanos=" +
                                        mainWebView.data_horario[mainWebView.data_position_horario]
                                        + "&cmbperiodos=" + mainWebView.periodo_horario[mainWebView.periodo_position_horario] + "&Exibir=OK");
                            }
                        }).setNegativeButton(R.string.dialog_cancel, null)
                        .show();
            });
        } else {
            ((MainActivity) getActivity()).fab_data.setClickable(false);
            ((MainActivity) getActivity()).fab_data.setOnClickListener(null);
        }
    }

    public void update(List<Horario> horario) {
        if (adapter != null) {
            this.horario = horario;
            adapter.update(this.horario);
            setFABListener();
        } else {
            setHorario(getView());
        }
    }
}
