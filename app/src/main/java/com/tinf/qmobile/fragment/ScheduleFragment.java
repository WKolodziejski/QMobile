package com.tinf.qmobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.matter.Schedule_;
import com.tinf.qmobile.utility.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.query.QueryBuilder;
import me.jlurena.revolvingweekview.WeekView;
import me.jlurena.revolvingweekview.WeekViewEvent;

import static com.tinf.qmobile.activity.calendar.EventCreateActivity.SCHEDULE;
import static com.tinf.qmobile.network.Client.pos;

public class ScheduleFragment extends Fragment {
    @BindView(R.id.weekView_horario)    WeekView weekView;
    private Bundle bundle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();

        BoxStore boxStore = DataBase.get().getBoxStore();

        boxStore.subscribe(Schedule.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> weekView.notifyDatasetChanged());

        boxStore.subscribe(Matter.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> weekView.notifyDatasetChanged());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weekView.setWeekViewLoader(ArrayList::new);

        weekView.setOnEventClickListener((event, eventRect) -> {
            Intent intent = new Intent(getActivity(), EventViewActivity.class);
            intent.putExtra("TYPE", SCHEDULE);
            intent.putExtra("ID", Long.valueOf(event.getIdentifier()));
            startActivity(intent);
        });


        updateSchedule();
    }

    private void updateSchedule() {
        new Thread(() -> {
            weekView.setWeekViewLoader(() -> {
                boolean[][] hours = new boolean[24][7];
                WeekViewEvent[] minutes = new WeekViewEvent[24];

                List<WeekViewEvent> events = new ArrayList<>();

                List<Schedule> schedules;

                if (bundle == null) {
                    schedules = DataBase.get().getBoxStore().boxFor(Schedule.class).query()
                            .equal(Schedule_.year, User.getYear(pos)).and()
                            .equal(Schedule_.period, User.getPeriod(pos))
                            .build().find();
                } else {

                    QueryBuilder<Schedule> builder = DataBase.get().getBoxStore().boxFor(Schedule.class).query()
                            .equal(Schedule_.year, User.getYear(pos)).and()
                            .equal(Schedule_.period, User.getPeriod(pos));

                    builder.link(Schedule_.matter)
                            .equal(Matter_.id, bundle.getLong("ID"));

                    schedules = builder.build().find();
                }

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

                    weekView.goToHour(firstIndex + (minutes[firstIndex].getStartTime().getMinute() * 0.0167));
                }

                return events;
            });

            weekView.post(() -> weekView.notifyDatasetChanged());
        }).start();
    }

}
