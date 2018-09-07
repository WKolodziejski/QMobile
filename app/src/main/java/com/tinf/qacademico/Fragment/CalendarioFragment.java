package com.tinf.qacademico.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.tinf.qacademico.Activity.CalendarioActivity;
import com.tinf.qacademico.Adapter.Calendario.CalendarioAdapter;
import com.tinf.qacademico.Class.Calendario.Evento;
import com.tinf.qacademico.Class.Calendario.Meses;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Data;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CalendarioFragment extends Fragment {
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    CalendarioAdapter adapter;
    List<Meses> mesesList;
    int month = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        setCalendar(view);

        return view;
    }

    private void setCalendar(View view) {

        mesesList = Data.loadCalendar(getContext());

        CompactCalendarView calendarView = ((CalendarioActivity) Objects.requireNonNull(getActivity())).calendar;
        calendarView.removeAllEvents();

        for (int i = 0; i < mesesList.size(); i++) {
            addEvents(calendarView, i);
        }

        Calendar lastDateMesesList = Calendar.getInstance();
        lastDateMesesList.set(Calendar.YEAR, mesesList.get(mesesList.size() - 1).getYear());
        lastDateMesesList.set(Calendar.MONTH, mesesList.get(mesesList.size() - 1).getMonth());
        lastDateMesesList.set(Calendar.DAY_OF_MONTH, mesesList.get(mesesList.size() - 1).getDias().get(0).getDia());

        if (new Date().getTime() < lastDateMesesList.getTimeInMillis()) {
            lastDateMesesList.setTimeInMillis(new Date().getTime());

            for (int i = 0; i < mesesList.size(); i++) {
                if (mesesList.get(i).getMonth() == lastDateMesesList.get(Calendar.MONTH)) {
                    month = i;
                    break;
                }
            }
        } else {
            month = mesesList.size() - 1;
        }

        adapter = new CalendarioAdapter(mesesList.get(month).getDias(), getActivity());

        calendarView.setCurrentDate(lastDateMesesList.getTime());

        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.shouldDrawIndicatorsBelowSelectedDays(true);

        ((CalendarioActivity) getActivity()).setTitle(dateFormatForMonth.format(lastDateMesesList.getTime()));


        RecyclerView recyclerViewCalendario = (RecyclerView) view.findViewById(R.id.recycler_calendario);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,
                false);

        recyclerViewCalendario.setAdapter(adapter);
        recyclerViewCalendario.setLayoutManager(layout);

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date date) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date.getTime());

                for (int i = 0; i < adapter.getCalendarioList().size(); i++) {
                    if (adapter.getCalendarioList().get(i).getDia() == calendar.get(Calendar.DAY_OF_MONTH)) {

                        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(Objects.requireNonNull(getActivity())) {
                            @Override
                            protected int getVerticalSnapPreference() {
                                return LinearSmoothScroller.SNAP_TO_START;
                            }
                        };

                        smoothScroller.setTargetPosition(i);
                        layout.startSmoothScroll(smoothScroller);
                        break;
                    }
                }
            }

            @Override
            public void onMonthScroll(Date date) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date.getTime());

                boolean isInRange = false;

                for (int i = 0; i < mesesList.size(); i++) {
                    if (calendar.get(Calendar.MONTH) == mesesList.get(i).getMonth()
                            && calendar.get(Calendar.YEAR) == mesesList.get(i).getYear()) {
                        isInRange = true;
                        break;
                    } else {
                        isInRange = false;
                    }
                }

                if (isInRange) {

                    ((CalendarioActivity) getActivity()).setTitle(dateFormatForMonth.format(date));

                    for (int i = 0; i < mesesList.size(); i++) {
                        if (mesesList.get(i).getYear() == calendar.get(Calendar.YEAR)) {
                            for (int j = i; j < mesesList.size(); j++) {
                                if (mesesList.get(j).getMonth() == calendar.get(Calendar.MONTH)) {
                                    adapter.update(mesesList.get(j).getDias());
                                    month = j;

                                    Calendar today = Calendar.getInstance();
                                    today.setTime(new Date());
                                    today.set(Calendar.HOUR_OF_DAY, 0);
                                    today.set(Calendar.MINUTE, 0);
                                    today.set(Calendar.SECOND, 0);

                                    for (int k = 0; k < mesesList.get(j).getDias().size(); k++) {
                                        if (calendar.getTimeInMillis() < today.getTimeInMillis()) {
                                            for (int l = 0; l < mesesList.get(j).getDias().get(k).getEventos().size(); l++) {
                                                mesesList.get(j).getDias().get(k).getEventos().get(l).setHappened(true);
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                } else {
                    //Negar deslizar fora do período
                }
            }
        });
    }

    private void addEvents(CompactCalendarView calendarView, int j) {
        for (int i = 0; i < mesesList.get(j).getDias().size(); i++) {
            Calendar date = Calendar.getInstance(Locale.getDefault());

            date.set(Calendar.MONTH, mesesList.get(j).getMonth());
            date.set(Calendar.ERA, GregorianCalendar.AD);
            date.set(Calendar.YEAR, mesesList.get(j).getYear());
            date.set(Calendar.DAY_OF_MONTH, i + 1);

            calendarView.addEvents(getEvents(date.getTimeInMillis(), j, i));
        }
    }

    private List<Event> getEvents(long timeInMillis, int j, int day) {
        List<Event> events = new ArrayList<>();

        for (int i = 0; i < mesesList.get(j).getDias().get(day).getEventos().size(); i++) {
            Evento e = mesesList.get(j).getDias().get(day).getEventos().get(i);
            events.add(new Event(e.getColor(), timeInMillis, null));
        }

        return events;
    }
}
