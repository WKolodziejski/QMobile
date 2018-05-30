package com.qacademico.qacademico.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewLoader;
import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Class.Horario;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Data;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.qacademico.qacademico.Utilities.Utils.pg_horario;
import static com.qacademico.qacademico.Utilities.Utils.url;


public class HorarioFragment extends Fragment {
    SingletonWebView mainWebView = SingletonWebView.getInstance();
    List<Horario> horario;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            horario = (List<Horario>) getArguments().getSerializable("Horario");
            setColors();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horario, container, false);

        setHorario(view);

        return view;
    }

    private void setHorario(View view) {

        if (horario != null) {

            if (horario.size() != 0) {

                ((MainActivity) Objects.requireNonNull(getActivity())).hideEmptyLayout();
                ((MainActivity) Objects.requireNonNull(getActivity())).dismissErrorConnection();

                setColors();

                WeekView weekView = (WeekView) view.findViewById(R.id.weekView_horario);

                Calendar firstDay = Calendar.getInstance();
                firstDay.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                weekView.goToDate(firstDay);

                weekView.setMonthChangeListener((newYear, newMonth) -> {

                    int firstHour = 0;

                    List<WeekViewEvent> week = new ArrayList<>();

                    for (int i = 0; i < horario.size(); i++) {
                        Calendar startTime = Calendar.getInstance();
                        startTime.set(Calendar.MONTH, newMonth - 1);
                        startTime.set(Calendar.YEAR, newYear);
                        startTime.set(Calendar.DAY_OF_WEEK, horario.get(i).getDay());
                        startTime.set(Calendar.HOUR_OF_DAY, trimh(trimta(horario.get(i).getDate())));
                        startTime.set(Calendar.MINUTE, trimm(trimta(horario.get(i).getDate())));

                        Calendar endTime = (Calendar) startTime.clone();
                        endTime.set(Calendar.MONTH, newMonth - 1);
                        endTime.set(Calendar.YEAR, newYear);
                        endTime.set(Calendar.HOUR_OF_DAY, trimh(trimtd(horario.get(i).getDate())));
                        endTime.set(Calendar.MINUTE, trimm(trimtd(horario.get(i).getDate())));

                        WeekViewEvent event = new WeekViewEvent(i, horario.get(i).getMateria(), startTime, endTime);
                        event.setColor(horario.get(i).getColor());

                        week.add(event);

                        if (startTime.get(Calendar.HOUR_OF_DAY) > firstHour) {
                            firstHour = startTime.get(Calendar.HOUR_OF_DAY);
                        }
                    }

                    weekView.goToHour(firstHour);
                    return week;
                });
            } else {
                ((MainActivity) Objects.requireNonNull(getActivity())).showEmptyLayout();
            }
        } else {
            ((MainActivity) Objects.requireNonNull(getActivity())).showErrorConnection();
        }
    }

    public void openDateDialog() {
        if (mainWebView.pg_horario_loaded && mainWebView.data_horario != null) {

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
                    .setCustomTitle(Utils.customAlertTitle(Objects.requireNonNull(getActivity()), R.drawable.ic_date_range_black_24dp,
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
        }
    }

    private void setColors() {
        for (int i = 0; i < horario.size(); i++) {
            if (horario.get(i).getColor() == 0) {
                for (int j = 0; j < horario.size(); j++) {
                    if (horario.get(i).getMateria().equals(horario.get(j).getMateria())) {
                        if (horario.get(j).getColor() == 0) {
                            if (horario.get(i).getColor() == 0) {
                                horario.get(i).setColor(Utils.getramdomColorGenerator(getContext()));
                                horario.get(j).setColor(horario.get(i).getColor());
                            } else {
                                horario.get(j).setColor(horario.get(i).getColor());
                            }
                        } else {
                            horario.get(i).setColor(horario.get(j).getColor());
                        }
                    } else {
                        if (horario.get(i).getColor() == 0) {
                            horario.get(i).setColor(Utils.getramdomColorGenerator(getContext()));
                        }
                    }
                }
            }
        }

        Data.saveObject(getContext(), horario, ".horario");
    }

    private int trimh(String string) {
        string = string.substring(0, string.indexOf(":"));
        return Integer.valueOf(string);
    }

    private int trimm(String string) {
        string = string.substring(string.indexOf(":") + 1);
        return Integer.valueOf(string);
    }

    private String trimta(String string) {
        string = string.substring(0, string.indexOf("~"));
        return string;
    }

    private String trimtd(String string) {
        string = string.substring(string.indexOf("~") + 1);
        return string;
    }
    public void update(List<Horario> horario) {
        this.horario = horario;
        setHorario(getView());
    }
}
