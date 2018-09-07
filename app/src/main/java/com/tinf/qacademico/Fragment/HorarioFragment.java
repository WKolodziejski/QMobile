package com.tinf.qacademico.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alamkanak.weekview.WeekView;
import com.tinf.qacademico.Class.Materias.Materia;
import com.tinf.qacademico.Widget.HorarioView;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Data;
import java.util.List;

public class HorarioFragment extends Fragment {
    public List<Materia> materias;

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
}
