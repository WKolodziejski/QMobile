package com.tinf.qacademico.Fragment;

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
import com.tinf.qacademico.Activity.HorarioActivity;
import com.tinf.qacademico.Class.Materias.Materia;
import com.tinf.qacademico.Widget.HorarioView;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Data;
import com.tinf.qacademico.Utilities.Utils;
import com.tinf.qacademico.WebView.SingletonWebView;
import java.util.List;
import java.util.Objects;
import static com.tinf.qacademico.Utilities.Utils.PG_HORARIO;
import static com.tinf.qacademico.Utilities.Utils.URL;

public class HorarioFragment extends Fragment implements HorarioActivity.OnPageUpdated {
    SingletonWebView webView = SingletonWebView.getInstance();
    public List<Materia> materias;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((HorarioActivity) Objects.requireNonNull(getActivity())).setOnPageFinishedListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horario, container, false);

        if (webView.infos.data_horario != null && webView.infos.periodo_horario != null) {
            materias = Data.loadMaterias(getContext(), webView.infos.data_horario[webView.data_position_horario]);
        }

        setHorario(view);

        return view;
    }

    private void setHorario(View view) {

        if (materias != null) {

            if (materias.size() != 0) {

                Objects.requireNonNull(((HorarioActivity) Objects.requireNonNull(getActivity())).getSupportActionBar())
                        .setTitle(webView.infos.data_horario[webView.data_position_horario] + " / "
                        + webView.infos.periodo_horario[webView.periodo_position_horario]);

                WeekView weekView = (WeekView) view.findViewById(R.id.weekView_horario);

                HorarioView.congifWeekView(weekView, materias);
            } else {
                //((MainActivity) Objects.requireNonNull(getActivity())).showEmptyLayout();
            }
        } else {
            //((MainActivity) Objects.requireNonNull(getActivity())).showRoundProgressbar();
        }
    }

    public void openDateDialog() {
        if (webView.pg_horario_loaded[webView.data_position_horario] && webView.infos.data_horario != null) {

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
                            R.string.dialog_date_change, R.color.colorPrimary))
                    .setPositiveButton(R.string.dialog_confirm, (dialog, which) -> {

                        webView.data_position_horario = year.getValue();
                        webView.periodo_position_horario = periodo.getValue();


                        if (webView.data_position_horario == Integer.parseInt(webView.infos.data_horario[0])) {
                            webView.loadUrl(URL + PG_HORARIO);
                        } else {
                            webView.loadUrl(URL + PG_HORARIO + "&COD_MATRICULA=-1&cmbanos=" +
                                    webView.infos.data_horario[webView.data_position_horario]
                                    + "&cmbperiodos=1&Exibir=OK");
                        }
                    }).setNegativeButton(R.string.dialog_cancel, null)
                    .show();
        }
    }

    @Override
    public void onPageUpdate(List<?> list) {

    }
}
