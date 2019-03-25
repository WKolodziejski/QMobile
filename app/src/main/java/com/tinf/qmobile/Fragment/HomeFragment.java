package com.tinf.qmobile.Fragment;

import android.app.Activity;
import android.app.ActivityOptions;
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
import me.jlurena.revolvingweekview.WeekView;
import me.jlurena.revolvingweekview.WeekViewEvent;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tinf.qmobile.Activity.CalendarioActivity;
import com.tinf.qmobile.Activity.HorarioActivity;
import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.Activity.Settings.EventActivity;
import com.tinf.qmobile.Adapter.Calendario.EventosAdapter;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.CalendarBase;
import com.tinf.qmobile.Class.Calendario.Event;
import com.tinf.qmobile.Class.Calendario.Event_;
import com.tinf.qmobile.Class.Calendario.Month;
import com.tinf.qmobile.Class.Calendario.Month_;
import com.tinf.qmobile.Class.Materias.Matter;
import com.tinf.qmobile.Class.Materias.Matter_;
import com.tinf.qmobile.Class.Materias.Schedule;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.Utilities.User;
import com.tinf.qmobile.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.tinf.qmobile.Network.Client.pos;
import static com.tinf.qmobile.Network.OnResponse.INDEX;
import static com.tinf.qmobile.Network.OnResponse.PG_LOGIN;
import static com.tinf.qmobile.Network.OnResponse.URL;

public class HomeFragment extends Fragment implements OnUpdate {
    private NestedScrollView nestedScrollView;
    private EventosAdapter calendarioAdapter;
    private List<Matter> matters;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) getActivity()).fab.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), EventActivity.class));
        });

        matters = App.getBox().boxFor(Matter.class).query()
                .equal(Matter_.year, User.getYear(pos)).and()
                .equal(Matter_.period, User.getPeriod(pos))
                .build().find();

        Calendar current = Calendar.getInstance();
        current.setTime(new Date());
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);

        List<Event> events = App.getBox().boxFor(Event.class).query()
                .greater(Event_.startTime, current.getTimeInMillis() - 1).build().find(0 ,5);

        if (events.size() < 5) {
            events = App.getBox().boxFor(Event.class).query().build().find();
            events = events.subList(events.size() - 5, events.size());
        }

        calendarioAdapter = new EventosAdapter(getActivity(), events);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nestedScrollView = (NestedScrollView) view.findViewById(R.id.home_scroll);

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    ((MainActivity) getActivity()).refreshLayout.setEnabled(scrollY == 0);
                });

        view.findViewById(R.id.home_website).setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(URL + INDEX + PG_LOGIN));
            startActivity(browserIntent);
        });

        view.post(() -> {
            ((MainActivity) getActivity()).fab.setImageResource(R.drawable.ic_add);
            ((MainActivity) getActivity()).fab.show();
        });

        showOffline(view);
        showHorario(view);
        showCalendar(view);

        LinearLayout horario = (LinearLayout) view.findViewById(R.id.home_horario);

        horario.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), HorarioActivity.class));
        });
    }

    private void showOffline(View view) {
        view.post(() -> {

            CardView offline = (CardView) view.findViewById(R.id.home_offline);

            if (!Client.isConnected() || (!Client.get().isValid() && !Client.get().isLogging())) {
                offline.setVisibility(View.VISIBLE);

                TextView text = (TextView) view.findViewById(R.id.offline_last_update);
                text.setText(String.format(getResources().getString(R.string.home_last_login), User.getLastLogin()));
            } else {
                offline.setVisibility(View.GONE);
            }
        });
    }

    private void showCalendar(View view) {

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_home);

            recyclerView.post(() -> {

            recyclerView.setAdapter(calendarioAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            LinearLayout calendario = (LinearLayout) view.findViewById(R.id.home_calendario);

            calendario.setOnClickListener(v -> {
                /*ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()), ((MainActivity) getActivity()).fab,
                                Objects.requireNonNull(ViewCompat.getTransitionName(((MainActivity) getActivity()).fab)));*/

                ActivityOptions options = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Pair statusAnim = Pair.create(recyclerView, recyclerView.getTransitionName());
                    Pair driverBundleAnim = Pair.create(((MainActivity) getActivity()).fab, ((MainActivity) getActivity()).fab.getTransitionName());
                    options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), statusAnim, driverBundleAnim);
                }

                startActivity(new Intent(getActivity(), CalendarioActivity.class), options.toBundle());

            });
        });
    }

    private void showHorario(View view) {

        WeekView weekView = (WeekView) view.findViewById(R.id.weekView_home);

        weekView.setWeekViewLoader(() -> {
            int firstHour = 24;

            List<WeekViewEvent> events = new ArrayList<>();

            for (Matter matter : matters) {
                for (Schedule schedule : matter.schedules) {
                    WeekViewEvent event = new WeekViewEvent(String.valueOf(schedule.id), matter.getTitle(),
                            schedule.getStartTime(), schedule.getEndTime());
                    event.setColor(matter.getColor());
                    events.add(event);

                    if (event.getStartTime().getHour() < firstHour) {
                        firstHour = event.getStartTime().getHour();
                    }
                }
            }

            weekView.goToHour(firstHour + 0.5);

            return events;
        });

        LinearLayout horario = (LinearLayout) view.findViewById(R.id.home_horario);

        horario.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()),
                            weekView, Objects.requireNonNull(ViewCompat.getTransitionName(weekView)));
            startActivity(new Intent(getActivity(), HorarioActivity.class), options.toBundle());
        });

    }

    @Override
    public void onScrollRequest() {
        if (nestedScrollView != null) {
            nestedScrollView.smoothScrollTo(0, 0);
        }
    }

    @Override
    public void onUpdate(int pg) {
        if (pg == UPDATE_REQUEST) {
            matters = App.getBox().boxFor(Matter.class).query()
                    .equal(Matter_.year, User.getYear(pos)).and()
                    .equal(Matter_.period, User.getPeriod(pos))
                    .build().find();
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
        ((MainActivity) getActivity()).addOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).addOnUpdateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).removeOnUpdateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).removeOnUpdateListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //((MainActivity) getActivity()).fab.hide();
    }
}
