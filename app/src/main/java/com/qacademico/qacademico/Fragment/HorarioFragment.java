package com.qacademico.qacademico.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
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


public class HorarioFragment extends Fragment implements MainActivity.OnPageUpdated {
    SingletonWebView webView = SingletonWebView.getInstance();
    public List<Horario> horarioList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) Objects.requireNonNull(getActivity())).setOnPageFinishedListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horario, container, false);

        if (webView.infos.data_horario != null && webView.infos.periodo_horario != null) {
            horarioList = (List<Horario>) Data.loadList(getContext(), Utils.HORARIO,
                    webView.infos.data_horario[0], webView.infos.periodo_horario[0]);
        } else if (((MainActivity) Objects.requireNonNull(getActivity())).navigation.getSelectedItemId() == R.id.navigation_horario) {
            ((MainActivity) Objects.requireNonNull(getActivity())).showErrorConnection();
        }

        setHorario(view);

        return view;
    }

    private void setHorario(View view) {

        if (horarioList != null) {

            if (horarioList.size() != 0) {

                Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar())
                        .setTitle(webView.infos.data_horario[webView.data_position_horario] + " / "
                        + webView.infos.periodo_horario[webView.periodo_position_horario]);
                ((MainActivity) Objects.requireNonNull(getActivity())).hideExpandBtn();
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

                    for (int i = 0; i < horarioList.size(); i++) {
                        Calendar startTime = Calendar.getInstance();
                        startTime.set(Calendar.MONTH, newMonth - 1);
                        startTime.set(Calendar.YEAR, newYear);
                        startTime.set(Calendar.DAY_OF_WEEK, horarioList.get(i).getDay());
                        startTime.set(Calendar.HOUR_OF_DAY, trimh(trimta(horarioList.get(i).getDate())));
                        startTime.set(Calendar.MINUTE, trimm(trimta(horarioList.get(i).getDate())));

                        Calendar endTime = (Calendar) startTime.clone();
                        endTime.set(Calendar.MONTH, newMonth - 1);
                        endTime.set(Calendar.YEAR, newYear);
                        endTime.set(Calendar.HOUR_OF_DAY, trimh(trimtd(horarioList.get(i).getDate())));
                        endTime.set(Calendar.MINUTE, trimm(trimtd(horarioList.get(i).getDate())));

                        WeekViewEvent event = new WeekViewEvent(i, horarioList.get(i).getMateria(), startTime, endTime);
                        event.setColor(horarioList.get(i).getColor());

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
            ((MainActivity) Objects.requireNonNull(getActivity())).showRoundProgressbar();
        }
    }

    public void openDateDialog() {
        if (webView.pg_horario_loaded && webView.infos.data_horario != null) {

            View theView = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);

            final NumberPicker year = (NumberPicker) theView.findViewById(R.id.year_picker);
            year.setMinValue(0);
            year.setMaxValue(webView.infos.data_horario.length - 1);
            year.setValue(webView.data_position_horario);
            year.setDisplayedValues(webView.infos.data_horario);
            year.setWrapSelectorWheel(false);

            final NumberPicker periodo = (NumberPicker) theView.findViewById(R.id.periodo_picker);
            periodo.setMinValue(0);
            periodo.setMaxValue(webView.infos.periodo_horario.length - 1);
            periodo.setValue(webView.periodo_position_horario);
            periodo.setDisplayedValues(webView.infos.periodo_horario);
            periodo.setWrapSelectorWheel(false);

            new AlertDialog.Builder(getActivity()).setView(theView)
                    .setCustomTitle(Utils.customAlertTitle(Objects.requireNonNull(getActivity()), R.drawable.ic_date_range_black_24dp,
                            R.string.dialog_date_change, R.color.horario_dialog))
                    .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {

                        webView.data_position_horario = year.getValue();
                        webView.periodo_position_horario = periodo.getValue();


                        if (webView.data_position_horario == Integer.parseInt(webView.infos.data_horario[0])) {
                            webView.html.loadUrl(url + pg_horario);
                        } else {
                            webView.html.loadUrl(url + pg_horario + "&COD_MATRICULA=-1&cmbanos=" +
                                    webView.infos.data_horario[webView.data_position_horario]
                                    + "&cmbperiodos=" + webView.infos.periodo_horario[webView.periodo_position_horario] + "&Exibir=OK");
                        }
                    }).setNegativeButton(R.string.dialog_cancel, null)
                    .show();
        }
    }

    private void setColors() {
        for (int i = 0; i < horarioList.size(); i++) {
            if (horarioList.get(i).getColor() == 0) {
                for (int j = 0; j < horarioList.size(); j++) {
                    if (horarioList.get(i).getMateria().equals(
                            horarioList.get(j).getMateria())) {
                        if (horarioList.get(j).getColor() == 0) {
                            if (horarioList.get(i).getColor() == 0) {
                                horarioList.get(i).setColor(Utils.getRandomColorGenerator(Objects.requireNonNull(getContext())));
                                horarioList.get(j).setColor(horarioList.get(i).getColor());
                            } else {
                                horarioList.get(j).setColor(horarioList.get(i).getColor());
                            }
                        } else {
                            horarioList.get(i).setColor(horarioList.get(j).getColor());
                        }
                    } else {
                        if (horarioList.get(i).getColor() == 0) {
                            horarioList.get(i).setColor(Utils.getRandomColorGenerator(Objects.requireNonNull(getContext())));
                        }
                    }
                }
            }
        }

        Data.saveList(Objects.requireNonNull(getContext()), horarioList,
                Utils.HORARIO, webView.infos.data_horario[webView.data_position_horario],
                webView.infos.periodo_horario[webView.periodo_position_horario]);
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

    @Override
    public void onPageUpdate(List<?> list) {
        horarioList = (List<Horario>) list;
        setHorario(getView());
    }
}
