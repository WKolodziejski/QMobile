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
    public List<Materia> materias;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((HorarioActivity) Objects.requireNonNull(getActivity())).setOnPageFinishedListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horario, container, false);

        setHorario(view);

        return view;
    }

    private void setHorario(View view) {

        materias = Data.loadMaterias(getContext());

        WeekView weekView = (WeekView) view.findViewById(R.id.weekView_horario);

        HorarioView.congifWeekView(weekView, materias);

    }

    @Override
    public void onPageUpdate(List<?> list) {

    }
}
