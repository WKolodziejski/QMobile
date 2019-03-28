package com.tinf.qmobile.Fragment;

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
import io.objectbox.Box;
import me.jlurena.revolvingweekview.WeekView;
import me.jlurena.revolvingweekview.WeekViewEvent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tinf.qmobile.Activity.Calendar.CalendarioActivity;
import com.tinf.qmobile.Activity.HorarioActivity;
import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.Activity.Calendar.CreateEventActivity;
import com.tinf.qmobile.Adapter.Calendario.EventosAdapter;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.Base.EventBase;
import com.tinf.qmobile.Class.Calendario.EventImage;
import com.tinf.qmobile.Class.Calendario.EventImage_;
import com.tinf.qmobile.Class.Calendario.EventQ;
import com.tinf.qmobile.Class.Calendario.EventQ_;
import com.tinf.qmobile.Class.Calendario.EventSimple;
import com.tinf.qmobile.Class.Calendario.EventSimple_;
import com.tinf.qmobile.Class.Calendario.EventUser;
import com.tinf.qmobile.Class.Calendario.EventUser_;
import com.tinf.qmobile.Class.Materias.Matter;
import com.tinf.qmobile.Class.Materias.Matter_;
import com.tinf.qmobile.Class.Materias.Schedule;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.Utilities.User;
import com.tinf.qmobile.R;

import org.threeten.bp.DayOfWeek;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
            startActivity(new Intent(getActivity(), CreateEventActivity.class));
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

        Box<EventUser> eventUserBox = App.getBox().boxFor(EventUser.class);
        Box<EventQ> eventQBox = App.getBox().boxFor(EventQ.class);
        Box<EventImage> eventImageBox = App.getBox().boxFor(EventImage.class);
        Box<EventSimple> eventSimpleBox = App.getBox().boxFor(EventSimple.class);

        List<EventBase> events = new ArrayList<>();

        events.addAll(eventUserBox.query().greater(EventUser_.startTime, current.getTimeInMillis() - 1).build().find());
        events.addAll(eventQBox.query().greater(EventQ_.startTime, current.getTimeInMillis() - 1).build().find());
        events.addAll(eventImageBox.query().greater(EventImage_.startTime, current.getTimeInMillis() - 1).build().find());
        events.addAll(eventSimpleBox.query().greater(EventSimple_.startTime, current.getTimeInMillis() - 1).build().find());

        Collections.sort(events, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));

        if (events.size() < 5) {
            events = new ArrayList<>();

            events.addAll(eventUserBox.query().build().find());
            events.addAll(eventQBox.query().build().find());
            events.addAll(eventImageBox.query().build().find());
            events.addAll(eventSimpleBox.query().build().find());

            Collections.sort(events, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));

            events = events.subList(events.size() - 5, events.size());
        } else {
            events = events.subList(0, 5);
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

        showHorario(view);

        view.post(() -> {
            nestedScrollView = (NestedScrollView) view.findViewById(R.id.home_scroll);

            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                    (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                        ((MainActivity) getActivity()).refreshLayout.setEnabled(scrollY == 0);
                    });

            showOffline(view);
            showCalendar(view);

            view.findViewById(R.id.home_website).setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(URL + INDEX + PG_LOGIN));
                startActivity(browserIntent);
            });
            ((MainActivity) getActivity()).fab.setImageResource(R.drawable.ic_add);
            ((MainActivity) getActivity()).fab.show();
        });
    }

    private void showOffline(View view) {

            CardView offline = (CardView) view.findViewById(R.id.home_offline);

            if (!Client.isConnected() || (!Client.get().isValid() && !Client.get().isLogging())) {
                offline.setVisibility(View.VISIBLE);

                TextView text = (TextView) view.findViewById(R.id.offline_last_update);
                text.setText(String.format(getResources().getString(R.string.home_last_login), User.getLastLogin()));
            } else {
                offline.setVisibility(View.GONE);
            }
    }

    private void showCalendar(View view) {

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_home);

            recyclerView.setAdapter(calendarioAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            LinearLayout calendario = (LinearLayout) view.findViewById(R.id.home_calendario);

            calendario.setOnClickListener(v -> {
                Pair statusAnim = Pair.create(recyclerView, recyclerView.getTransitionName());
                Pair driverBundleAnim = Pair.create(((MainActivity) getActivity()).fab, ((MainActivity) getActivity()).fab.getTransitionName());

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), statusAnim, driverBundleAnim);
                startActivity(new Intent(getActivity(), CalendarioActivity.class), options.toBundle());
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

            weekView.goToDate(DayOfWeek.MONDAY);
            weekView.goToHour(firstHour + 0.5);

            return events;
        });

        LinearLayout horario = (LinearLayout) view.findViewById(R.id.home_horario);

        horario.setOnClickListener(v -> {
            Pair statusAnim = Pair.create(weekView, weekView.getTransitionName());
            Pair driverBundleAnim = Pair.create(((MainActivity) getActivity()).fab, ((MainActivity) getActivity()).fab.getTransitionName());

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), statusAnim, driverBundleAnim);
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
