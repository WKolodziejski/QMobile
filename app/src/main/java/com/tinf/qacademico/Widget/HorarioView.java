package com.tinf.qacademico.Widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.tinf.qacademico.Class.Materias.Materia;
import com.tinf.qacademico.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HorarioView extends WeekView {

    public HorarioView(Context context) {
        super(context);
    }

    public HorarioView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorarioView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static void congifWeekView(WeekView weekView, List<Materia> materias) {

        Calendar currentWeek = Calendar.getInstance();
        currentWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        weekView.goToDate(currentWeek);

        weekView.setMonthChangeListener((newYear, newMonth) -> {

            int firstHour = 24;

            List<WeekViewEvent> week = new ArrayList<>();

            for (int i = 0; i < materias.size(); i++) {
                for (int j = 0; j < materias.get(i).horarios.size(); j++) {
                    Calendar startTime = Calendar.getInstance();
                    startTime.set(Calendar.YEAR, newYear);
                    startTime.set(Calendar.MONTH, newMonth - 1);
                    startTime.set(Calendar.DAY_OF_WEEK, materias.get(i).horarios.get(j).getDay());
                    startTime.set(Calendar.HOUR_OF_DAY, materias.get(i).horarios.get(j).getStartHour());
                    startTime.set(Calendar.MINUTE, materias.get(i).horarios.get(j).getStartMinute());

                    Calendar endTime = (Calendar) startTime.clone();
                    endTime.set(Calendar.HOUR_OF_DAY, materias.get(i).horarios.get(j).getEndHour());
                    endTime.set(Calendar.MINUTE, materias.get(i).horarios.get(j).getEndMinute());

                    WeekViewEvent event = new WeekViewEvent(i, materias.get(i).getName(), startTime, endTime);
                    event.setColor(materias.get(i).getColor());

                    week.add(event);

                    if (startTime.get(Calendar.HOUR_OF_DAY) < firstHour) {
                        firstHour = startTime.get(Calendar.HOUR_OF_DAY);
                    }
                }
            }

            weekView.goToHour(firstHour + 0.5);

            return week;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
