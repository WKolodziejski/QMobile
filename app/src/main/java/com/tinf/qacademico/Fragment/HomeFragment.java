package com.tinf.qacademico.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alamkanak.weekview.WeekView;
import com.tinf.qacademico.Activity.CalendarioActivity;
import com.tinf.qacademico.Activity.HorarioActivity;
import com.tinf.qacademico.Activity.MainActivity;
import com.tinf.qacademico.Adapter.Calendario.CalendarioAdapter;
import com.tinf.qacademico.Class.Calendario.Meses;
import com.tinf.qacademico.Class.Materias.Materia;
import com.tinf.qacademico.Widget.HorarioView;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Data;
import com.tinf.qacademico.WebView.SingletonWebView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.view.View.GONE;

public class HomeFragment extends Fragment implements MainActivity.OnPageUpdated {
    CalendarioAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) Objects.requireNonNull(getActivity())).setOnPageUpdateListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        showHorario(view);
        showEvents(view);

        return view;
    }

    private void showEvents(View view) {

        List<Meses> mesesList = Data.loadCalendar(getContext());

        RecyclerView recyclerViewCalendario = (RecyclerView) view.findViewById(R.id.recycler_home);

        if (mesesList.isEmpty()) {
            recyclerViewCalendario.setVisibility(GONE);
            return;
        }

        int month = 0;

        Calendar lastDateMesesList = Calendar.getInstance();
        lastDateMesesList.set(Calendar.YEAR, mesesList.get(mesesList.size() - 1).getYear());
        lastDateMesesList.set(Calendar.MONTH, mesesList.get(mesesList.size() - 1).getMonth());
        lastDateMesesList.set(Calendar.DAY_OF_MONTH, mesesList.get(mesesList.size() - 1).getDias().get(0).getDia());

        if (new Date().getTime() < lastDateMesesList.getTimeInMillis()) {
            lastDateMesesList.setTimeInMillis(new Date().getTime());

            for (int i = 0; i < mesesList.size(); i++) {
                if (mesesList.get(i).getMonth() == lastDateMesesList.get(Calendar.MONTH)) {
                    month = i;
                    break;
                }
            }
        } else {
            month = mesesList.size() - 1;
        }

        adapter = new CalendarioAdapter(mesesList.get(month).getDias(), getActivity());

        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,
                false);

        recyclerViewCalendario.setAdapter(adapter);
        recyclerViewCalendario.setLayoutManager(layout);

        LinearLayout calendario = (LinearLayout) view.findViewById(R.id.home_calendario);

        calendario.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()),
                            recyclerViewCalendario, Objects.requireNonNull(ViewCompat.getTransitionName(recyclerViewCalendario)));
            startActivity(new Intent(getActivity(), CalendarioActivity.class), options.toBundle());
            ((MainActivity)getActivity()).dismissProgressbar();
        });
    }

    private void showHorario(View view) {

        WeekView weekView = (WeekView) view.findViewById(R.id.weekView_home);

        HorarioView.congifWeekView(weekView, Data.loadMaterias(getContext()));

        LinearLayout horario = (LinearLayout) view.findViewById(R.id.home_horario);

        horario.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()),
                            weekView, Objects.requireNonNull(ViewCompat.getTransitionName(weekView)));
            startActivity(new Intent(getActivity(), HorarioActivity.class), options.toBundle());
            ((MainActivity)getActivity()).dismissProgressbar();
        });
    }

    @Override
    public void onPageUpdate(List<?> list) {

    }
}
