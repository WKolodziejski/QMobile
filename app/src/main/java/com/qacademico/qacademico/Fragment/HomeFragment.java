package com.qacademico.qacademico.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Activity.MateriaisActivity;
import com.qacademico.qacademico.Adapter.Home.ShortcutAdapter;
import com.qacademico.qacademico.Class.Horario;
import com.qacademico.qacademico.Class.Shortcut;
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

        SwipeRefreshLayout mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(Objects.requireNonNull(getActivity())::recreate);

        ShortcutAdapter adapter = new ShortcutAdapter(getShortcuts(), getActivity());
        adapter.setOnClickListener(this);

        RecyclerView recyclerViewShortcut = (RecyclerView) view.findViewById(R.id.recycler_shortcut);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        recyclerViewShortcut.setAdapter(adapter);
        recyclerViewShortcut.setLayoutManager(layout);

        Button calendario = (Button) view.findViewById(R.id.calendario_abrir);

        calendario.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CalendarioActivity.class));
        });

        updateHeaderStatus(view);
        displayHorario(view);

        return view;
    }

    public void displayHorario(View view) {

        if (webView.infos.data_horario != null && webView.infos.periodo_horario != null) {
            horarioList = (List<Horario>) Data.loadList(getContext(), Utils.HORARIO,
                    webView.infos.data_horario[0], webView.infos.periodo_horario[0]);
            setColors();
        }

        WeekView weekView = (WeekView) view.findViewById(R.id.weekView_home);

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

            weekView.goToHour(firstHour - 3.5);
            return week;
        });
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

    protected List<Shortcut> getShortcuts() { //Configura os botões da página Home
        List<Shortcut> shortcuts = new ArrayList<>();

        shortcuts.add(new Shortcut(getResources().getString(R.string.title_materiais), R.drawable.ic_closed_diary));
        shortcuts.add(new Shortcut(getResources().getString(R.string.title_horario), R.drawable.ic_access_alarm_black_24dp));
        shortcuts.add(new Shortcut(getResources().getString(R.string.title_documentos), R.drawable.ic_check_form));

        return shortcuts;
    }
}
