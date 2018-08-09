package com.qacademico.qacademico.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alamkanak.weekview.WeekView;
import com.qacademico.qacademico.Activity.HorarioActivity;
import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Class.Horario;
import com.qacademico.qacademico.Custom.Widget.CustomWeekView;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Data;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;

import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements MainActivity.OnPageUpdated {
    SingletonWebView webView = SingletonWebView.getInstance();
    public List<Horario> horarioList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) Objects.requireNonNull(getActivity())).setOnPageUpdateListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ((MainActivity)getActivity()).tabLayout.setVisibility(View.GONE);

        SharedPreferences login_info = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.LOGIN_INFO, 0);

        Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar())
                .setTitle(login_info.getString(Utils.LOGIN_NAME, ""));

        ((MainActivity) Objects.requireNonNull(getActivity())).hideExpandBtn();
        ((MainActivity) Objects.requireNonNull(getActivity())).hideEmptyLayout();
        ((MainActivity) Objects.requireNonNull(getActivity())).dismissErrorConnection();
        ((MainActivity) Objects.requireNonNull(getActivity())).dismissLinearProgressbar();
        ((MainActivity)getActivity()).hideCalendar();

        LinearLayout horario = (LinearLayout) view.findViewById(R.id.home_horario);
        horario.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), HorarioActivity.class));
        });

        displayHorario(view);

        return view;
    }

    public void displayHorario(View view) {

        if (webView.infos.data_horario != null && webView.infos.periodo_horario != null) {
            horarioList = (List<Horario>) Data.loadList(getContext(), Utils.HORARIO,
                    webView.infos.data_horario[0], webView.infos.periodo_horario[0]);
        }

        WeekView weekView = (WeekView) view.findViewById(R.id.weekView_home);

        CustomWeekView.congifWeekView(weekView, horarioList);
    }

    @Override
    public void onPageUpdate(List<?> list) {

    }
}
