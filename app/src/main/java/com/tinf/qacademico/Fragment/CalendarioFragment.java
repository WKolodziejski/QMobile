package com.tinf.qacademico.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.tinf.qacademico.Activity.MainActivity;
import com.tinf.qacademico.Adapter.Calendario.CalendarioAdapter;
import com.tinf.qacademico.Class.Calendario.Dia;
import com.tinf.qacademico.Class.Calendario.Evento;
import com.tinf.qacademico.Class.Calendario.Meses;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Data;
import com.tinf.qacademico.Utilities.Utils;
import com.tinf.qacademico.WebView.SingletonWebView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CalendarioFragment extends Fragment implements MainActivity.OnPageUpdated {
    SingletonWebView webView = SingletonWebView.getInstance();
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    CalendarioAdapter adapter;
    List<Meses> calendario;
    int month = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) Objects.requireNonNull(getActivity())).setOnPageUpdateListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        if (webView.infos.data_calendario != null) {
            calendario = (List<Meses>) Data.loadList(getContext(), Utils.CALENDARIO,
                    webView.infos.data_calendario, null);
        }

        setCalendar(view);

        return view;
    }

    private void setCalendar(View view) {

        CompactCalendarView compactCalendar = ((MainActivity) Objects.requireNonNull(getActivity())).calendar;

        compactCalendar.removeAllEvents();

        for (int i = 0; i < calendario.size(); i++) {
            addEvents(compactCalendar, i);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendario.get(calendario.size() - 1).getYear());
        calendar.set(Calendar.MONTH, calendario.get(calendario.size() - 1).getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, calendario.get(calendario.size() - 1).getDias().get(0).getDia());

        if (compactCalendar.getFirstDayOfCurrentMonth().getTime() < calendar.getTimeInMillis()) {
            calendar.setTimeInMillis(compactCalendar.getFirstDayOfCurrentMonth().getTime());

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(calendar.getTimeInMillis());

            for (int i = 0; i < calendario.size(); i++) {
                if (calendario.get(i).getMonth() == cal.get(Calendar.MONTH)) {
                    month = i;
                    break;
                }
            }
        } else {
            month = calendario.size() - 1;
        }

        adapter = new CalendarioAdapter(calendario.get(month).getDias(), getActivity());

        Date date = new Date();
        date.setTime(calendar.getTimeInMillis());

        compactCalendar.setCurrentDate(date);
        compactCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        ((AppCompatActivity) getActivity()).setTitle(dateFormatForMonth.format(date));

        RecyclerView recyclerViewCalendario = (RecyclerView) view.findViewById(R.id.recycler_calendario);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,
                false);

        recyclerViewCalendario.setAdapter(adapter);
        recyclerViewCalendario.setLayoutManager(layout);

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {

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

                for (int i = 0; i < calendario.size(); i++) {
                    if (calendar.get(Calendar.MONTH) == calendario.get(i).getMonth()
                            && calendar.get(Calendar.YEAR) == calendario.get(i).getYear()) {
                        isInRange = true;
                        break;
                    } else {
                        isInRange = false;
                    }
                }

                if (isInRange) {

                    ((MainActivity) getActivity()).setTitle(dateFormatForMonth.format(date));

                    for (int i = 0; i < calendario.size(); i++) {
                        if (calendario.get(i).getYear() == calendar.get(Calendar.YEAR)) {
                            for (int j = i; j < calendario.size(); j++) {
                                if (calendario.get(j).getMonth() == calendar.get(Calendar.MONTH)) {
                                    adapter.update(calendario.get(j).getDias());
                                    month = j;
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

    private void addEvents(CompactCalendarView compactCalendar, int j) {
        for (int i = 0; i < calendario.get(j).getDias().size(); i++) {
            Calendar date = Calendar.getInstance(Locale.getDefault());

            date.set(Calendar.MONTH, calendario.get(j).getMonth());
            date.set(Calendar.ERA, GregorianCalendar.AD);
            date.set(Calendar.YEAR, calendario.get(j).getYear());
            date.set(Calendar.DAY_OF_MONTH, i + 1);

            compactCalendar.addEvents(getEvents(date.getTimeInMillis(), j, i));
        }
    }

    private List<Event> getEvents(long timeInMillis, int j, int day) {
        List<Event> events = new ArrayList<>();

        for (int i = 0; i < calendario.get(j).getDias().get(day).getEventos().size(); i++) {
            Evento e = calendario.get(j).getDias().get(day).getEventos().get(i);
            events.add(new Event(Color.argb(255, 255, 0, 0), timeInMillis, null));
        }

        return events;
    }

    @Override
    public void onPageUpdate(List<?> list) {
        if (list.get(0) instanceof Meses) {
            calendario = (List<Meses>) list;
            adapter.update(calendario.get(month).getDias());
        }
    }
}
