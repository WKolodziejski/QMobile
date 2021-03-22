package com.tinf.qmobile.fragment;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.CalendarActivity;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.activity.ScheduleActivity;
import com.tinf.qmobile.adapter.HomeAdapter;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.fragment.dialog.CreateFragment;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.matter.Schedule_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;

import org.threeten.bp.DayOfWeek;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataSubscription;
import me.jlurena.revolvingweekview.WeekView;
import me.jlurena.revolvingweekview.WeekViewEvent;

import static com.tinf.qmobile.activity.EventCreateActivity.SCHEDULE;
import static com.tinf.qmobile.network.Client.pos;

public class HomeFragment extends Fragment implements OnUpdate {
    @BindView(R.id.weekView_home)       WeekView weekView;
    @BindView(R.id.home_scroll)         NestedScrollView nestedScrollView;
    @BindView(R.id.fab_home)            FloatingActionButton fab;
    @BindView(R.id.recycler_home)       RecyclerView recyclerView;
    @BindView(R.id.schedule_empty_text) TextView empty;
    @BindView(R.id.home_calendar)       LinearLayout calendar;
    @BindView(R.id.home_horario)        LinearLayout schedule;
    private DataSubscription sub1, sub2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendar.setVisibility(pos == 0 ? View.VISIBLE : View.GONE);

        weekView.setWeekViewLoader(ArrayList::new);

        weekView.setOnTouchListener((view1, motionEvent) -> {
            if (motionEvent.getAxisValue(MotionEvent.AXIS_Y) <= 90) {
                schedule.dispatchTouchEvent(motionEvent);
                return true;

            } else {
                if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL)
                    schedule.dispatchTouchEvent(motionEvent);

                return false;
            }
        });

        weekView.setOnEventClickListener((event, eventRect) -> {
            Log.d("WEEK", event.getName());
            Intent intent = new Intent(getActivity(), EventViewActivity.class);
            intent.putExtra("TYPE", SCHEDULE);
            intent.putExtra("ID", Long.valueOf(event.getIdentifier()));
            startActivity(intent);
        });

        weekView.setWeekViewLoader(() -> {

            boolean[][] hours = new boolean[24][7];
            WeekViewEvent[] minutes = new WeekViewEvent[24];

            List<WeekViewEvent> events = new ArrayList<>();
            List<Schedule> schedules = DataBase.get().getBoxStore().boxFor(Schedule.class).query()
                    .equal(Schedule_.year, User.getYear(pos)).and()
                    .equal(Schedule_.period, User.getPeriod(pos))
                    .build().find();

            ViewGroup.LayoutParams params = weekView.getLayoutParams();

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

                params.height = Math.round((((minutes[lastIndex].getEndTime().getHour() * 60) + minutes[lastIndex].getEndTime().getMinute())
                        - ((minutes[firstIndex].getStartTime().getHour() * 60) + minutes[firstIndex].getStartTime().getMinute()) + 45)
                        * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));

                weekView.goToDay(DayOfWeek.MONDAY);
                weekView.goToHour(firstIndex + (minutes[firstIndex].getStartTime().getMinute() * 0.0167));

                empty.setVisibility(View.GONE);
            } else {
                params.height = Math.round((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);

                empty.setVisibility(View.VISIBLE);
            }

            weekView.setLayoutParams(params);

            return events;
        });

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    ((MainActivity) getActivity()).refreshLayout.setEnabled(scrollY == 0);

                    if (scrollY < oldScrollY && !fab.isShown())
                        fab.show();
                    else if(scrollY > oldScrollY && fab.isShown())
                        fab.hide();
                });

        fab.setOnClickListener(v -> new CreateFragment().show(getChildFragmentManager(), "sheet_create"));

        LinearLayout offline = (LinearLayout) view.findViewById(R.id.home_offline);

        if (!Client.isConnected() || (!Client.get().isValid() && !Client.get().isLogging())) {
            offline.setVisibility(View.VISIBLE);

            TextView text = (TextView) view.findViewById(R.id.offline_last_update);
            text.setText(String.format(getResources().getString(R.string.home_last_login), User.getLastLogin()));
        } else {
            offline.setVisibility(View.GONE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(new HomeAdapter(getContext()));
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
        startActivity(new Intent(getActivity(), ScheduleActivity.class),
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
        weekView.notifyDatasetChanged();
        calendar.setVisibility(pos == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sub1 = DataBase.get().getBoxStore().subscribe(Schedule.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> weekView.notifyDatasetChanged());

        sub2 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> weekView.notifyDatasetChanged());
    }

    @Override
    public void onStart() {
        super.onStart();
        Client.get().addOnUpdateListener(this);
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
