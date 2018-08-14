package com.qacademico.qacademico.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.Adapter.Calendario.CalendarioAdapter;
import com.qacademico.qacademico.Adapter.ExpandableListAdapter;
import com.qacademico.qacademico.Class.Calendario.Dia;
import com.qacademico.qacademico.Class.Calendario.Evento;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class CalendarioFragment extends Fragment {
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    CalendarioAdapter adapter;
    List<Dia> calendario = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        setCalendar(view);

        return view;
    }

    private void setCalendar(View view) {

        CompactCalendarView compactCalendar = ((MainActivity)getActivity()).calendar;
        addEvents(compactCalendar, Calendar.DECEMBER, 2018);
        addEvents(compactCalendar, Calendar.AUGUST, 2018);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(dateFormatForMonth.format(compactCalendar.getFirstDayOfCurrentMonth()));

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> bookingsFromMap = compactCalendar.getEvents(dateClicked);
                Log.d("CALENDAR", "inside onclick " + dateFormatForDisplaying.format(dateClicked));
                /*if (bookingsFromMap != null) {
                    Log.d("CALENDAR", bookingsFromMap.toString());
                    mutableBookings.clear();
                    for (Event booking : bookingsFromMap) {
                        mutableBookings.add((String) booking.getData());
                    }
                    adapter.notifyDataSetChanged();
                }*/

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });

        RecyclerView recyclerViewCalendario = (RecyclerView) view.findViewById(R.id.recycler_calendario);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        adapter = new CalendarioAdapter(calendario, getActivity());

        recyclerViewCalendario.setAdapter(adapter);
        recyclerViewCalendario.setLayoutManager(layout);
        //recyclerViewDiarios.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    private void addEvents(CompactCalendarView compactCalendar,int  month, int year) {
        Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
        currentCalender.setTime(new Date());
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = currentCalender.getTime();
        for (int i = 0; i < 6; i++) {
            currentCalender.setTime(firstDayOfMonth);
            if (month > -1) {
                currentCalender.set(Calendar.MONTH, month);
            }
            if (year > -1) {
                currentCalender.set(Calendar.ERA, GregorianCalendar.AD);
                currentCalender.set(Calendar.YEAR, year);
            }
            currentCalender.add(Calendar.DATE, i);
            //setToMidnight(currentCalender);
            long timeInMillis = currentCalender.getTimeInMillis();

            List<Event> events = getEvents(timeInMillis, i);

            List<Evento> eventos = new ArrayList<>();

            for (int j = 0; j < events.size(); j++) {
                eventos.add(new Evento(events.get(j).getData().toString().substring(0, 18), events.get(j).getData().toString().substring(20), events.get(j).getColor()));
            }

            calendario.add(new Dia(i + 1, eventos));

            compactCalendar.addEvents(events);
        }
    }

    private List<Event> getEvents(long timeInMillis, int day) {
        if (day < 2) {
            return Arrays.asList(new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)));
        } else if ( day > 2 && day <= 4) {
            return Arrays.asList(
                    new Event(Color.argb(255, 255, 0, 255), timeInMillis, "Event at " + new Date(timeInMillis)),
                    new Event(Color.argb(255, 255, 255, 100), timeInMillis, "Event 2 at " + new Date(timeInMillis)));
        } else {
            return Arrays.asList(
                    new Event(Color.argb(255, 255, 0, 0), timeInMillis, "Event at " + new Date(timeInMillis) ),
                    new Event(Color.argb(255, 0, 0, 255), timeInMillis, "Event 2 at " + new Date(timeInMillis)),
                    new Event(Color.argb(255, 0, 255, 0), timeInMillis, "Event 3 at " + new Date(timeInMillis)));
        }
    }
}
