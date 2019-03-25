package com.tinf.qmobile.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.objectbox.Box;
import me.jlurena.revolvingweekview.WeekView;
import me.jlurena.revolvingweekview.WeekViewEvent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tinf.qmobile.Activity.HorarioActivity;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.Event;
import com.tinf.qmobile.Class.Materias.Matter;
import com.tinf.qmobile.Class.Materias.Matter_;
import com.tinf.qmobile.Class.Materias.Schedule;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.tinf.qmobile.Network.Client.pos;
import static com.tinf.qmobile.Network.OnResponse.PG_HORARIO;

public class HorarioFragment extends Fragment implements OnUpdate {
    private List<Matter> matters;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        matters = App.getBox().boxFor(Matter.class).query()
                .equal(Matter_.year, User.getYear(pos)).and()
                .equal(Matter_.period, User.getPeriod(pos))
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
    }

    /*private void showHorario(View view) {

        WeekView weekView = (WeekView) view.findViewById(R.id.weekView_horario);

        weekView.setMonthChangeListener((startDate, endDate) -> {

            int firstHour = 24;

            List<WeekViewDisplayable> weekHorario = new ArrayList<>();

            for (int i = 0; i < matters.size(); i++) {
                for (int j = 0; j < matters.get(i).schedules.size(); j++) {
                    Calendar startTime = matters.get(i).schedules.get(j).getStartTime(startDate.get(Calendar.MONTH));
                    Calendar endTime =  matters.get(i).schedules.get(j).getEndTime(startDate.get(Calendar.MONTH));

                    WeekViewEvent event = new WeekViewEvent(matters.get(i).schedules.get(j).id, matters.get(i).getTitle(), startTime, endTime);
                    event.setColor(matters.get(i).getColor());

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
    }*/

    private void showHorario2(View view) {
        WeekView weekView = (WeekView) view.findViewById(R.id.weekView_horario);

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
    }

    @Override
    public void onUpdate(int pg) {
        if (pg == PG_HORARIO || pg == UPDATE_REQUEST) {
            matters = App.getBox().boxFor(Matter.class).query()
                    .equal(Matter_.year, User.getYear(pos)).and()
                    .equal(Matter_.period, User.getPeriod(pos))
                    .build().find();
            showHorario2(getView());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((HorarioActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HorarioActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
    }

    @Override
    public void onScrollRequest() {}
}
