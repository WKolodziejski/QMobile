package com.tinf.qacademico.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alamkanak.weekview.WeekView;
import com.tinf.qacademico.Activity.MainActivity;
import com.tinf.qacademico.Widget.HorarioView;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Data;

import io.objectbox.BoxStore;

public class HorarioFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horario, container, false);

        setHorario(view);

        return view;
    }

    private void setHorario(View view) {

        WeekView weekView = (WeekView) view.findViewById(R.id.weekView_horario);

        HorarioView.congifWeekView(weekView, Data.loadMaterias(getContext()));

    }

    private BoxStore getBox() {
        return ((MainActivity) getActivity()).getBox();
    }
}
