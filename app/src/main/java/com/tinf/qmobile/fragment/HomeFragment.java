package com.tinf.qmobile.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.CalendarActivity;
import com.tinf.qmobile.activity.HorarioActivity;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.activity.MatterActivity;
import com.tinf.qmobile.activity.EventCreateActivity;
import com.tinf.qmobile.activity.WebViewActivity;
import com.tinf.qmobile.adapter.EventsAdapter;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.model.calendar.Day;
import com.tinf.qmobile.model.calendar.EventImage;
import com.tinf.qmobile.model.calendar.EventImage_;
import com.tinf.qmobile.model.calendar.EventSimple;
import com.tinf.qmobile.model.calendar.EventSimple_;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.calendar.EventUser_;
import com.tinf.qmobile.model.calendar.Header;
import com.tinf.qmobile.model.calendar.Month;
import com.tinf.qmobile.model.calendar.Month_;
import com.tinf.qmobile.model.calendar.base.CalendarBase;
import com.tinf.qmobile.model.calendar.base.EventBase;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.journal.Journal_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.matter.Schedule_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;
import org.threeten.bp.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataSubscription;
import me.jlurena.revolvingweekview.WeekView;
import me.jlurena.revolvingweekview.WeekViewEvent;
import static com.tinf.qmobile.activity.EventCreateActivity.EVENT;
import static com.tinf.qmobile.model.calendar.Utils.getDate;
import static com.tinf.qmobile.network.Client.pos;

public class HomeFragment extends Fragment implements OnUpdate {
    @BindView(R.id.weekView_home)       WeekView weekView;
    @BindView(R.id.home_scroll)         NestedScrollView nestedScrollView;
    @BindView(R.id.fab_home)            ExtendedFloatingActionButton fab;
    @BindView(R.id.recycler_home)       RecyclerView recyclerView;
    private DataSubscription sub1, sub2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(User.getName());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weekView.setWeekViewLoader(ArrayList::new);

        updateSchedule();

        weekView.setOnEventClickListener((event, eventRect) -> {
            Matter matter = DataBase.get().getBoxStore().boxFor(Matter.class).query()
                    .equal(Matter_.title_, event.getName())
                    .equal(Matter_.year_, User.getYear(pos)).and()
                    .equal(Matter_.period_, User.getPeriod(pos))
                    .build().findUnique();
            if (matter != null) {
                Intent intent = new Intent(getContext(), MatterActivity.class);
                intent.putExtra("ID", matter.id);
                intent.putExtra("PAGE", MatterActivity.SCHEDULE);
                startActivity(intent);
            }
        });

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    ((MainActivity) getActivity()).refreshLayout.setEnabled(scrollY == 0);
                    if (scrollY < oldScrollY && !fab.isShown())
                        fab.show();
                    else if(scrollY > oldScrollY && fab.isShown())
                        fab.hide();
                });

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EventCreateActivity.class);
            intent.putExtra("TYPE", EVENT);
            startActivity(intent);
        });

        CardView offline = (CardView) view.findViewById(R.id.home_offline);

        if (!Client.isConnected() || (!Client.get().isValid() && !Client.get().isLogging())) {
            offline.setVisibility(View.VISIBLE);

            TextView text = (TextView) view.findViewById(R.id.offline_last_update);
            text.setText(String.format(getResources().getString(R.string.home_last_login), User.getLastLogin()));
        } else {
            offline.setVisibility(View.GONE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new EventsAdapter(getContext(), true));
    }

    /*private List<CalendarBase> getCalendarList() {
        Calendar current = Calendar.getInstance();
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);

        Box<EventUser> eventUserBox = DataBase.get().getBoxStore().boxFor(EventUser.class);
        Box<Journal> eventJournalBox = DataBase.get().getBoxStore().boxFor(Journal.class);
        Box<EventImage> eventImageBox = DataBase.get().getBoxStore().boxFor(EventImage.class);
        Box<EventSimple> eventSimpleBox = DataBase.get().getBoxStore().boxFor(EventSimple.class);
        Box<Month> monthBox = DataBase.get().getBoxStore().boxFor(Month.class);

        List<CalendarBase> events = new ArrayList<>();

        events.addAll(eventUserBox.query().greater(EventUser_.startTime, current.getTimeInMillis() - 1).build().find());
        events.addAll(eventJournalBox.query().greater(Journal_.startTime, current.getTimeInMillis() - 1).build().find());
        events.addAll(eventImageBox.query().greater(EventImage_.startTime, current.getTimeInMillis() - 1).build().find());
        events.addAll(eventSimpleBox.query().greater(EventSimple_.startTime, current.getTimeInMillis() - 1).build().find());
        events.addAll(monthBox.query().greater(Month_.time, current.getTimeInMillis() - 1).build().find());

        Collections.sort(events, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));

        for (int i = 0; i < events.size(); i++) {
            CalendarBase e1 = events.get(i);

            int j = i + 1;

            if (j < events.size()) {

                CalendarBase e2 = events.get(j);

                while (e1.getDay() == e2.getDay() && e1.getYear() == e2.getYear()) {
                    e2 = events.get(++j);
                }
            }

            events.add(i, new Header(getDate(e1.getDate(), true)));

            i = j;
        }

        List<CalendarBase> returnn = new ArrayList<>();

        if (!events.isEmpty()) {
            int k = 0;
            int l = 0;

            while (l < 5 && k < events.size() - 1) {
                CalendarBase e = events.get(k);

                while (!(e instanceof EventBase) && k < events.size() - 1) {
                    e = events.get(++k);
                }

                if (k < events.size()) {

                    if (e instanceof EventBase) {

                        if (k >= 2) {
                            CalendarBase m = events.get(k - 2);

                            if (m instanceof Month)
                                returnn.add(m);
                        }

                        if (k >= 1) {
                            CalendarBase d = events.get(k - 1);

                            if (d instanceof Header)
                                returnn.add(d);
                        }

                        returnn.add(e);
                    }

                    k++;
                    l++;
                }
            }
        }
        return returnn;
    }*/

    private void updateSchedule() {
        new Thread(() -> {
            weekView.setWeekViewLoader(() -> {
                boolean[][] hours = new boolean[24][7];
                WeekViewEvent[] minutes = new WeekViewEvent[24];

                List<WeekViewEvent> events = new ArrayList<>();
                List<Schedule> schedules = DataBase.get().getBoxStore().boxFor(Schedule.class).query()
                        .equal(Schedule_.year, User.getYear(pos)).and()
                        .equal(Schedule_.period, User.getPeriod(pos))
                        .build().find();

                if (!schedules.isEmpty()) {

                    for (Schedule schedule : schedules) {
                        WeekViewEvent event = new WeekViewEvent(String.valueOf(schedule.id), schedule.getTitle(),
                                schedule.getStartTime(), schedule.getEndTime());
                        event.setColor(schedule.getColor());
                        events.add(event);

                        int day = event.getStartTime().getDay().getValue();
                        int hour = event.getStartTime().getHour();

                        if (!hours[hour][day]) {
                            minutes[hour] = event;
                            hours[hour][day] = true;
                        }
                    }

                    int firstIndex = 0;
                    int parc1 = 0;

                    for (int h = 0; h < 24; h++) {
                        int sum = 0;
                        for (int d = 0; d < 7; d++) {
                            if (hours[h][d]) {
                                sum++;
                            }
                        }
                        if (sum > parc1) {
                            firstIndex = h;
                            parc1 = sum;
                        }
                    }

                    boolean r = true;
                    int d1 = 0;

                    while (r) {
                        if (!hours[firstIndex - 1][d1] && d1 < 7) {
                            d1++;
                            if (d1 == 7)
                                r = false;
                        } else {
                            firstIndex--;
                            d1 = 0;
                        }
                    }

                    int lastIndex = firstIndex;

                    for (int h = firstIndex; h < 24; h++) {
                        int sum = 0;
                        for (int d = 0; d < 7; d++) {
                            if (hours[h][d])
                                sum++;
                        }
                        if (sum == 0)
                            break;
                        else
                            lastIndex = h;
                    }

                    ViewGroup.LayoutParams params = weekView.getLayoutParams();
                    params.height = Math.round((((minutes[lastIndex].getEndTime().getHour() * 60) + minutes[lastIndex].getEndTime().getMinute()) - ((minutes[firstIndex].getStartTime().getHour() * 60) + minutes[firstIndex].getStartTime().getMinute()) + 45) * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                    weekView.setLayoutParams(params);

                    weekView.goToDay(DayOfWeek.MONDAY);
                    weekView.goToHour(firstIndex + (minutes[firstIndex].getStartTime().getMinute() * 0.0167));

                }

                Log.d("Home", "View listener");

                return events;
            });

            weekView.post(() -> weekView.notifyDatasetChanged());

        }).start();
    }

    @OnClick(R.id.home_website)
    public void openQSite(View view) {
        startActivity(new Intent(getContext(), WebViewActivity.class));
    }

    @OnClick(R.id.home_calendario)
    public void openCalendar(View view) {
        startActivity(new Intent(getActivity(), CalendarActivity.class),
                ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                        Pair.create(fab, fab.getTransitionName()))
                        .toBundle());
    }

    @OnClick(R.id.home_horario)
    public void openSchedule(View view) {
        startActivity(new Intent(getActivity(), HorarioActivity.class),
                ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                        Pair.create(fab, fab.getTransitionName()))
                        .toBundle());
    }

    @Override
    public void onScrollRequest() {
        if (nestedScrollView != null) {
            nestedScrollView.smoothScrollTo(0, 0);
        }
    }

    @Override
    public void onDateChanged() {
        updateSchedule();
    }

    @Override
    public void onStart() {
        super.onStart();
        Client.get().addOnUpdateListener(this);

        BoxStore boxStore = DataBase.get().getBoxStore();

        sub1 = boxStore.subscribe(Schedule.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> {
                    updateSchedule();
                });

        sub2 = boxStore.subscribe(Matter.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> {
                    updateSchedule();
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        Client.get().addOnUpdateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sub1.cancel();
        sub2.cancel();
    }

}
