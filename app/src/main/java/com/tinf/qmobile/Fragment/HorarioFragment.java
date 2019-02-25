package com.tinf.qmobile.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;
import com.tinf.qmobile.Activity.HorarioActivity;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Matter;
import com.tinf.qmobile.Class.Materias.Matter_;
import com.tinf.qmobile.Interfaces.OnUpdate;
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
    private int firstHour = 24;

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
        showHorario(view);
    }

    private void showHorario(View view) {

        WeekView weekView = (WeekView) view.findViewById(R.id.weekView_horario);

        weekView.setMonthChangeListener((startDate, endDate) -> {

            List<WeekViewDisplayable> weekHorario = new ArrayList<>();

            for (int i = 0; i < matters.size(); i++) {
                for (int j = 0; j < matters.get(i).horarios.size(); j++) {
                    Calendar startTime = Calendar.getInstance();
                    startTime.set(Calendar.MONTH, startDate.get(Calendar.MONTH));
                    startTime.set(Calendar.DAY_OF_WEEK, matters.get(i).horarios.get(j).getDay());
                    startTime.set(Calendar.HOUR_OF_DAY, matters.get(i).horarios.get(j).getStartHour());
                    startTime.set(Calendar.MINUTE, matters.get(i).horarios.get(j).getStartMinute());

                    Calendar endTime = (Calendar) startTime.clone();
                    endTime.set(Calendar.HOUR_OF_DAY, matters.get(i).horarios.get(j).getEndHour());
                    endTime.set(Calendar.MINUTE, matters.get(i).horarios.get(j).getEndMinute());

                    WeekViewEvent event = new WeekViewEvent(matters.get(i).horarios.get(j).id, matters.get(i).getTitle(), startTime, endTime);
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
    }

    @Override
    public void onUpdate(int pg) {
        if (pg == PG_HORARIO || pg == UPDATE_REQUEST) {
            matters = App.getBox().boxFor(Matter.class).query()
                    .equal(Matter_.year, User.getYear(pos)).and()
                    .equal(Matter_.period, User.getPeriod(pos))
                    .build().find();
            showHorario(getView());
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
