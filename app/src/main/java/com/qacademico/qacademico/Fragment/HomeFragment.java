package com.qacademico.qacademico.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.qacademico.qacademico.Activity.CalendarioActivity;
import com.qacademico.qacademico.Activity.HorarioActivity;
import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Adapter.Home.ShortcutAdapter;
import com.qacademico.qacademico.Class.Diarios.Horario;
import com.qacademico.qacademico.Class.Shortcut;
import com.qacademico.qacademico.Custom.Widget.CustomWeekView;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Data;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements MainActivity.OnPageUpdated, ShortcutAdapter.OnShortcutClicked {
    SingletonWebView webView = SingletonWebView.getInstance();
    public List<Horario> horarioList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) Objects.requireNonNull(getActivity())).setOnPageFinishedListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Objects.requireNonNull(((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar())
                .setTitle(getResources().getString(R.string.title_home));
        ((MainActivity) Objects.requireNonNull(getActivity())).hideExpandBtn();
        ((MainActivity) Objects.requireNonNull(getActivity())).hideEmptyLayout();
        ((MainActivity) Objects.requireNonNull(getActivity())).dismissErrorConnection();
        ((MainActivity) Objects.requireNonNull(getActivity())).dismissLinearProgressbar();
        
        ShortcutAdapter adapter = new ShortcutAdapter(getShortcuts(), getActivity());
        adapter.setOnClickListener(this);

        RecyclerView recyclerViewShortcut = (RecyclerView) view.findViewById(R.id.recycler_shortcut);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        recyclerViewShortcut.setAdapter(adapter);
        recyclerViewShortcut.setLayoutManager(layout);

        Button calendario = (Button) view.findViewById(R.id.calendario_abrir);
        Button horario = (Button) view.findViewById(R.id.horario_abrir);

        calendario.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CalendarioActivity.class));
        });

        horario.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), HorarioActivity.class));
        });

        updateHeaderStatus(view);
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

    public void updateHeaderStatus(View view) {

        SharedPreferences login_info = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.LOGIN_INFO, 0);

        TextView msg = (TextView) view.findViewById(R.id.welcome_msg);
        TextView status = (TextView) view.findViewById(R.id.updated_status);

        String period_of_day = "";

        Calendar rightNow = Calendar.getInstance();
        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);

        if (currentHour >= 6 && currentHour < 12) {
            period_of_day = getResources().getString(R.string.home_welcome_message_dia);
        } else if (currentHour >= 12 && currentHour < 19) {
            period_of_day = getResources().getString(R.string.home_welcome_message_tarde);
        } else if (currentHour >= 19 || currentHour < 6) {
            period_of_day = getResources().getString(R.string.home_welcome_message_noite);
        }

        if (!login_info.getString(Utils.LOGIN_NAME, "").equals("")) {
            msg.setText(String.format(getResources().getString(R.string.home_welcome_message),
                    period_of_day, login_info.getString(Utils.LOGIN_NAME, "")));
        } else {
            msg.setText(period_of_day);
        }

        if (login_info.getInt(Utils.LOGIN_DAY, rightNow.get(Calendar.DAY_OF_YEAR)) != rightNow.get(Calendar.DAY_OF_YEAR)) {
            status.setText("Atualizado " + (rightNow.get(Calendar.DAY_OF_YEAR) - login_info.getInt(Utils.LOGIN_DAY, 0))
                    + " dias atrás");
        } else {
            if (login_info.getInt(Utils.LOGIN_HOUR, 0) > rightNow.get(Calendar.HOUR_OF_DAY)) {
                status.setText("Atualizado " + (rightNow.get(Calendar.HOUR_OF_DAY) - login_info.getInt(Utils.LOGIN_HOUR, 0))
                        + " horas atrás");
            } else {
                String hour = String.valueOf(login_info.getInt(Utils.LOGIN_HOUR, 0));
                String minute = String.valueOf(login_info.getInt(Utils.LOGIN_MINUTE, 0));

                if (hour.length() < 2) {
                    hour = "0" + hour;
                }

                if (minute.length() < 2) {
                    minute = "0" + minute;
                }

                status.setText("Atualizado às " + hour + ":" + minute);
            }
        }

        LinearLayout offilne = (LinearLayout) view.findViewById(R.id.offline);

        if (Utils.isConnected(getActivity()) || ((MainActivity)getActivity()).webView.pg_home_loaded) {
            offilne.setVisibility(View.GONE);
        } else {
            offilne.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageUpdate(List<?> list) {
        updateHeaderStatus(getView());
    }

    @Override
    public void OnShortcutClick(int position, View view) {

    }

    protected List<Shortcut> getShortcuts() { //Configura os botões da página Home
        List<Shortcut> shortcuts = new ArrayList<>();

        shortcuts.add(new Shortcut(getResources().getString(R.string.title_materiais), R.drawable.ic_closed_diary));
        shortcuts.add(new Shortcut(getResources().getString(R.string.title_horario), R.drawable.ic_access_alarm_black_24dp));
        shortcuts.add(new Shortcut(getResources().getString(R.string.title_documentos), R.drawable.ic_check_form));

        return shortcuts;
    }
}
