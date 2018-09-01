package com.tinf.qacademico.Custom.Widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.tinf.qacademico.Class.Materias.Horario;
import com.tinf.qacademico.Class.Materias.Materia;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CustomWeekView extends WeekView {
    public CustomWeekView(Context context) {
        super(context);
    }

    public CustomWeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomWeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static void congifWeekView(WeekView weekView, List<Materia> materias) {

        Calendar firstDay = Calendar.getInstance();
        firstDay.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        weekView.goToDate(firstDay);

        weekView.setMonthChangeListener((newYear, newMonth) -> {

            int firstHour = 24;

            List<WeekViewEvent> week = new ArrayList<>();

            for (int i = 0; i < materias.size(); i++) {
                for (int j = 0; j < materias.get(i).getHorarios().size(); j++) {
                    Calendar startTime = (Calendar) materias.get(i).getHorarios().get(j).getStartTime().clone();
                    startTime.set(Calendar.MONTH, newMonth - 1);
                    startTime.set(Calendar.YEAR, newYear);

                    Calendar endTime = (Calendar) materias.get(i).getHorarios().get(j).getEndTime().clone();
                    endTime.set(Calendar.MONTH, newMonth - 1);
                    endTime.set(Calendar.YEAR, newYear);

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
