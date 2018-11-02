package com.tinf.qacademico.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.tinf.qacademico.Activity.CalendarioActivity;
import com.tinf.qacademico.Adapter.Calendario.CalendarioAdapter;
import com.tinf.qacademico.Class.Calendario.Evento;
import com.tinf.qacademico.Class.Calendario.Mes;
import com.tinf.qacademico.Class.Calendario.Mes_;
import com.tinf.qacademico.R;
import com.tinf.qacademico.WebView.SingletonWebView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CalendarioFragment extends Fragment {
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM ― yyyy", Locale.getDefault());
    private CalendarioAdapter adapter;
    private List<Mes> mesesList;
    private int month = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCalendar(view);
    }

    private void setCalendar(View view) {

        mesesList = getBox().boxFor(Mes.class).query().build().find();

        CompactCalendarView calendarView = ((CalendarioActivity) Objects.requireNonNull(getActivity())).calendar;
        calendarView.removeAllEvents();

        for (int i = 0; i < mesesList.size(); i++) {
            addEvents(calendarView, i);
        }

        Calendar lastDateMesesList = Calendar.getInstance();
        lastDateMesesList.set(Calendar.YEAR, mesesList.get(mesesList.size() - 1).getYear());
        lastDateMesesList.set(Calendar.MONTH, mesesList.get(mesesList.size() - 1).getMonth());
        lastDateMesesList.set(Calendar.DAY_OF_MONTH, mesesList.get(mesesList.size() - 1).days.get(0).getDay());

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

        adapter = new CalendarioAdapter(getActivity(), mesesList.get(month).days, false);

        calendarView.setCurrentDate(lastDateMesesList.getTime());

        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.shouldDrawIndicatorsBelowSelectedDays(true);

        ((CalendarioActivity) getActivity()).setTitle(dateFormatForMonth.format(lastDateMesesList.getTime()));


        RecyclerView recyclerViewCalendario = (RecyclerView) view.findViewById(R.id.recycler_calendario);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,
                false);

        recyclerViewCalendario.setAdapter(adapter);
        recyclerViewCalendario.setLayoutManager(layout);

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date date) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date.getTime());

                for (int i = 0; i < adapter.getCalendarioList().size(); i++) {
                    if (adapter.getCalendarioList().get(i).getDay() == calendar.get(Calendar.DAY_OF_MONTH)) {

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
                int direction = 0; // 1 Direita      -1 Esquerda

                for (int i = 0; i < mesesList.size(); i++) {
                    if (calendar.get(Calendar.MONTH) == mesesList.get(i).getMonth()
                            && calendar.get(Calendar.YEAR) == mesesList.get(i).getYear()) {
                        isInRange = true;
                        break;
                    } else {
                        if (calendar.get(Calendar.YEAR) == mesesList.get(i).getYear()) {
                            direction = -1;
                        } else {
                            direction = 1;
                        }
                        isInRange = false;
                    }
                }

                if (isInRange) {

                    ((CalendarioActivity) getActivity()).setTitle(dateFormatForMonth.format(date));

                    for (int i = 0; i < mesesList.size(); i++) {
                        if (mesesList.get(i).getYear() == calendar.get(Calendar.YEAR)) {
                            for (int j = i; j < mesesList.size(); j++) {
                                if (mesesList.get(j).getMonth() == calendar.get(Calendar.MONTH)) {
                                    adapter = new CalendarioAdapter(getActivity(), mesesList.get(j).days, false);
                                    recyclerViewCalendario.setAdapter(adapter);
                                    month = j;

                                    Calendar today = Calendar.getInstance();
                                    today.setTime(new Date());

                                    for (int k = 0; k < mesesList.get(j).days.size(); k++) {
                                        if (calendar.getTimeInMillis() <= today.getTimeInMillis()) {
                                            for (int l = 0; l < mesesList.get(j).days.get(k).eventos.size(); l++) {
                                                if (mesesList.get(j).days.get(k).eventos.get(l).day.getTarget().getDay() <= today.get(Calendar.DAY_OF_MONTH)) {

                                                    mesesList.get(j).days.get(k).eventos.get(l).setHappened(true);

                                                    Box<Evento> eventoBox = getBox().boxFor(Evento.class);

                                                    Evento evento = eventoBox.get(mesesList.get(j).days.get(k).eventos.get(l).id);
                                                    evento.setHappened(true);

                                                    eventoBox.put(evento);
                                                }
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
                    Toast.makeText(getContext(), "Fim do período letivo", Toast.LENGTH_SHORT).show();
                    if (direction == 1) {
                        calendarView.scrollRight();
                        calendarView.scrollLeft();
                    } else if (direction == -1) {
                        calendarView.scrollLeft();
                        calendarView.scrollRight();
                    }
                }
            }
        });
    }

    private void addEvents(CompactCalendarView calendarView, int j) {
        for (int i = 0; i < mesesList.get(j).days.size(); i++) {
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

        for (int i = 0; i < mesesList.get(j).days.get(day).eventos.size(); i++) {
            Evento e = mesesList.get(j).days.get(day).eventos.get(i);
            events.add(new Event(e.getColor(), timeInMillis, null));
        }

        return events;
    }

    private BoxStore getBox() {
        return ((CalendarioActivity) getActivity()).getBox();
    }
}
