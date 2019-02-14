package com.tinf.qmobile.Fragment;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;
import com.tinf.qmobile.Activity.CalendarioActivity;
import com.tinf.qmobile.Activity.HorarioActivity;
import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.Adapter.Calendario.CalendarioAdapter;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.Mes;
import com.tinf.qmobile.Class.Calendario.Mes_;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.Interfaces.OnUpdate;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.Utilities.User;
import com.tinf.qmobile.Utilities.Utils;
import com.tinf.qmobile.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.tinf.qmobile.Network.Client.PG_HOME;
import static com.tinf.qmobile.Network.Client.PG_LOGIN;
import static com.tinf.qmobile.Network.Client.URL;
import static com.tinf.qmobile.Utilities.Utils.UPDATE_REQUEST;

public class HomeFragment extends Fragment implements OnUpdate {
    private NestedScrollView nestedScrollView;
    private CalendarioAdapter calendarioAdapter;
    private List<Materia> materias;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        materias = App.getBox().boxFor(Materia.class).query().equal(Materia_.year,
                Client.getYear()).build().find();

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Mes mes = App.getBox().boxFor(Mes.class).query()
                .equal(Mes_.year, today.get(Calendar.YEAR))
                .equal(Mes_.month, today.get(Calendar.MONTH))
                .build().findUnique();

        if (mes == null) {
            List<Mes> mesList = App.getBox().boxFor(Mes.class).query().build().find();
            mes = mesList.get(mesList.size() - 1);
        }

        calendarioAdapter = new CalendarioAdapter(getActivity(), mes.days, true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).setTitle(User.getName());

        nestedScrollView = (NestedScrollView) view.findViewById(R.id.home_scroll);

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    ((MainActivity) getActivity()).refreshLayout.setEnabled(scrollY == 0);
                });

        view.findViewById(R.id.home_website).setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(URL + PG_LOGIN));
            startActivity(browserIntent);
        });

        showOffline(view);
        showHorario(view);
        showCalendar(view);
    }

    private void showOffline(View view) {
        view.post(() -> {

            CardView offline = (CardView) view.findViewById(R.id.home_offline);

            if (!Client.isConnected()) {
                offline.setVisibility(View.VISIBLE);

                TextView text = (TextView) view.findViewById(R.id.offline_last_update);

                Date date = new Date(User.getLastLogin());

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                text.setText(String.format(getResources().getString(R.string.home_last_login), format.format(date)));
            } else {
                offline.setVisibility(View.GONE);
            }
        });
    }

    private void showCalendar(View view) {

            RecyclerView recyclerViewCalendario = (RecyclerView) view.findViewById(R.id.recycler_home);

            recyclerViewCalendario.post(() -> {

            RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,
                    false);

            recyclerViewCalendario.setAdapter(calendarioAdapter);
            recyclerViewCalendario.setLayoutManager(layout);

            LinearLayout calendario = (LinearLayout) view.findViewById(R.id.home_calendario);

            calendario.setOnClickListener(v -> {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()), recyclerViewCalendario,
                                Objects.requireNonNull(ViewCompat.getTransitionName(recyclerViewCalendario)));
                startActivity(new Intent(getActivity(), CalendarioActivity.class), options.toBundle());
            });
        });
    }

    private void showHorario(View view) {

        WeekView weekView = (WeekView) view.findViewById(R.id.weekView_home);

        weekView.post(() -> {

            weekView.setMonthChangeListener((startDate, endDate) -> {

                int firstHour = 24;

                List<WeekViewDisplayable> weekHorario = new ArrayList<>();

                for (int i = 0; i < materias.size(); i++) {
                    for (int j = 0; j < materias.get(i).horarios.size(); j++) {
                        Calendar startTime = Calendar.getInstance();
                        startTime.set(Calendar.MONTH, startDate.get(Calendar.MONTH));
                        startTime.set(Calendar.DAY_OF_WEEK, materias.get(i).horarios.get(j).getDay());
                        startTime.set(Calendar.HOUR_OF_DAY, materias.get(i).horarios.get(j).getStartHour());
                        startTime.set(Calendar.MINUTE, materias.get(i).horarios.get(j).getStartMinute());

                        Calendar endTime = (Calendar) startTime.clone();
                        endTime.set(Calendar.HOUR_OF_DAY, materias.get(i).horarios.get(j).getEndHour());
                        endTime.set(Calendar.MINUTE, materias.get(i).horarios.get(j).getEndMinute());

                        WeekViewEvent event = new WeekViewEvent(materias.get(i).horarios.get(j).id, materias.get(i).getName(), startTime, endTime);
                        event.setColor(getResources().getColor(materias.get(i).getColor()));

                        weekHorario.add(event);

                        if (startTime.get(Calendar.HOUR_OF_DAY) < firstHour) {
                            firstHour = startTime.get(Calendar.HOUR_OF_DAY);
                        }
                    }
                }

                Calendar currentWeek = Calendar.getInstance();
                currentWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                weekView.goToDate(currentWeek);
                weekView.goToHour(firstHour);

                return weekHorario;
            });

            weekView.notifyDataSetChanged();

            LinearLayout horario = (LinearLayout) view.findViewById(R.id.home_horario);

            horario.setOnClickListener(v -> {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()),
                                weekView, Objects.requireNonNull(ViewCompat.getTransitionName(weekView)));
                startActivity(new Intent(getActivity(), HorarioActivity.class), options.toBundle());
            });
        });
    }

    @Override
    public void onUpdate(int pg) {
        if (pg == UPDATE_REQUEST) {
            materias = App.getBox().boxFor(Materia.class).query().equal(Materia_.year,
                    Client.getYear()).build().find();
        }
        if (pg == PG_LOGIN || pg == UPDATE_REQUEST) {
            if (getView() != null) {
                showHorario(getView());
                showOffline(getView());
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
    }

    @Override
    public void requestScroll() {
        nestedScrollView.smoothScrollTo(0,0);
    }
}
