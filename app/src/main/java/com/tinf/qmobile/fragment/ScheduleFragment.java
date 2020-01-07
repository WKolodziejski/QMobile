package com.tinf.qmobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.activity.calendar.EventCreateActivity;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.matter.Schedule_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import me.jlurena.revolvingweekview.WeekView;
import me.jlurena.revolvingweekview.WeekViewEvent;

import static com.tinf.qmobile.App.getBox;
import static com.tinf.qmobile.activity.calendar.EventCreateActivity.SCHEDULE;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.network.OnResponse.PG_HORARIO;

public class ScheduleFragment extends Fragment implements OnUpdate {
    private Bundle bundle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_horario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showHorario2(view);
    }

    private void showHorario2(View view) {
        WeekView weekView = (WeekView) view.findViewById(R.id.weekView_horario);

        List<Schedule> schedules;

        if (bundle == null) {
            schedules = App.getBox().boxFor(Schedule.class).query()
                    .equal(Schedule_.year, User.getYear(pos)).and()
                    .equal(Schedule_.period, User.getPeriod(pos))
                    .build().find();
        } else {

            QueryBuilder<Schedule> builder =  App.getBox().boxFor(Schedule.class).query()
                    .equal(Schedule_.year, User.getYear(pos)).and()
                    .equal(Schedule_.period, User.getPeriod(pos));

            builder.link(Schedule_.matter)
                    .equal(Matter_.id, bundle.getLong("ID"));

            schedules = builder.build().find();
        }

        new Thread(() -> {
            int firstHour = 24;

            List<WeekViewEvent> events = new ArrayList<>();

            for (Schedule schedule : schedules) {
                WeekViewEvent event = new WeekViewEvent(String.valueOf(schedule.id), schedule.getTitle(),
                        schedule.getStartTime(), schedule.getEndTime());
                event.setColor(schedule.getColor());
                events.add(event);

                if (event.getStartTime().getHour() < firstHour) {
                    firstHour = event.getStartTime().getHour();
                    firstHour += event.getStartTime().getMinute() * 0.0167;
                }
            }

            weekView.goToHour(firstHour);

            weekView.post(() -> {
                weekView.invalidate();
                weekView.setWeekViewLoader(() -> events);
                weekView.notifyDatasetChanged();
            });

        }).start();

        weekView.setWeekViewLoader(ArrayList::new);

        weekView.setOnEventClickListener((event, eventRect) -> {
            //Matter matter = getBox().boxFor(Schedule.class).get(Long.valueOf(event.getIdentifier())).matter.getTarget();

            Intent intent = new Intent(getActivity(), EventViewActivity.class);
            intent.putExtra("TYPE", SCHEDULE);
            intent.putExtra("ID", Long.valueOf(event.getIdentifier()));
            startActivity(intent);

            /*if (matter != null) {
                Intent intent = new Intent(getContext(), MateriaActivity.class);
                intent.putExtra("ID", matter.id);
                startActivity(intent);
            }*/
        });

    }

    @Override
    public void onUpdate(int pg) {
        if (pg == PG_HORARIO || pg == UPDATE_REQUEST) {
            if (getView() != null)
                showHorario2(getView());
        }
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
    public void onStop() {
        super.onStop();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onScrollRequest() {}

}
