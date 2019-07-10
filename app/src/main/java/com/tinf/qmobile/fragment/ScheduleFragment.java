package com.tinf.qmobile.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.objectbox.Box;
import me.jlurena.revolvingweekview.WeekView;
import me.jlurena.revolvingweekview.WeekViewEvent;

import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinf.qmobile.activity.HorarioActivity;
import com.tinf.qmobile.App;
import com.tinf.qmobile.activity.MateriaActivity;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.R;
import com.tinf.qmobile.utility.User;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.tinf.qmobile.App.getBox;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.network.OnResponse.PG_HORARIO;
import static java.util.Calendar.MONDAY;

public class ScheduleFragment extends Fragment implements OnUpdate {
    private List<Matter> matters;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        matters = getBox().boxFor(Matter.class).query().order(Matter_.title_)
                .equal(Matter_.year_, User.getYear(pos)).and()
                .equal(Matter_.period_, User.getPeriod(pos))
                .build().find();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_horario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showHorario2(view);

        /*FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_add_schedule);
        fab.setOnClickListener(v -> {
            //startActivity(new Intent(getActivity(), CreateEventActivity.class));
        });*/
    }

    private void showHorario2(View view) {
        WeekView weekView = (WeekView) view.findViewById(R.id.weekView_horario);

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        //prefs.edit().putInt(SCHEDULE_HOUR, 0).apply();

        weekView.setWeekViewLoader(() -> {
            //double firstHour = 24;
            int firstHour = 24;

            List<WeekViewEvent> events = new ArrayList<>();

            for (Matter matter : matters) {
                for (Schedule schedule : matter.schedules) {
                    WeekViewEvent event = new WeekViewEvent(String.valueOf(schedule.id), matter.getTitle(),
                            schedule.getStartTime(), schedule.getEndTime());
                    event.setColor(matter.getColor());
                    events.add(event);

                    /*if (event.getStartTime().getHour() < firstHour) {
                        firstHour = event.getStartTime().getHour() + event.getStartTime().getMinute();
                    }*/

                    if (event.getStartTime().getHour() < firstHour) {
                        firstHour = event.getStartTime().getHour();
                        firstHour += event.getStartTime().getMinute() * 0.0167;
                    }
                }
            }

            weekView.goToDay(DayOfWeek.MONDAY);
            weekView.goToHour(firstHour);

            weekView.setOnEventClickListener((event, eventRect) -> {
                Matter matter = getBox().boxFor(Matter.class).query()
                        .equal(Matter_.title_, event.getName())
                        .equal(Matter_.year_, User.getYear(pos)).and()
                        .equal(Matter_.period_, User.getPeriod(pos))
                        .build().findUnique();
                if (matter != null) {
                    Intent intent = new Intent(getContext(), MateriaActivity.class);
                    intent.putExtra("ID", matter.id);
                    startActivity(intent);
                }
            });
            /*weekView.setDayTimeInterpreter(new WeekView.DayTimeInterpreter() {
                @Override
                public String interpretDay(int day) {
                    DayOfWeek dayOfWeek = DayOfWeek.of(day);
                    return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault());
                }

                @Override
                public String interpretTime(int hour, int minutes) {
                    LocalTime time = LocalTime.of(hour, minutes);
                    return time.format(DateFormat.is24HourFormat(getContext()) ? DateTimeFormatter.ofPattern("H") : DateTimeFormatter.ofPattern("ha"));
                }
            });*/


            /*if (firstHour != prefs.getFloat(SCHEDULE_HOUR, 24)) {
                weekView.goToHour(firstHour + 0.5);
            }*/

            return events;
        });
    }

    @Override
    public void onUpdate(int pg) {
        if (pg == PG_HORARIO || pg == UPDATE_REQUEST) {
            matters = App.getBox().boxFor(Matter.class).query()
                    .equal(Matter_.year_, User.getYear(pos)).and()
                    .equal(Matter_.period_, User.getPeriod(pos))
                    .build().find();
            showHorario2(getView());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //((HorarioActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //((HorarioActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
    }

    @Override
    public void onScrollRequest() {}
}