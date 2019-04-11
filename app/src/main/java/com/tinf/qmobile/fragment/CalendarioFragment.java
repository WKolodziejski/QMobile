package com.tinf.qmobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import io.objectbox.Box;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tinf.qmobile.activity.calendar.CalendarioActivity;
import com.tinf.qmobile.activity.calendar.EventCreateActivity;
import com.tinf.qmobile.adapter.calendario.EventosAdapter;
import com.tinf.qmobile.model.calendario.Base.CalendarBase;
import com.tinf.qmobile.App;
import com.tinf.qmobile.model.calendario.Base.EventBase;
import com.tinf.qmobile.model.calendario.Day;
import com.tinf.qmobile.model.calendario.EventImage;
import com.tinf.qmobile.model.calendario.EventJournal;
import com.tinf.qmobile.model.calendario.EventSimple;
import com.tinf.qmobile.model.calendario.EventUser;
import com.tinf.qmobile.model.calendario.Month;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.tinf.qmobile.activity.calendar.EventCreateActivity.EVENT;
import static com.tinf.qmobile.network.OnResponse.PG_CALENDARIO;

public class CalendarioFragment extends Fragment implements OnUpdate {
    private CompactCalendarView calendarView;
    private LinearLayoutManager layout;
    private EventosAdapter adapter;
    //private CalendarAdapterTEST adapter;
    private List<CalendarBase> events;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        layout = new LinearLayoutManager(getContext());

        calendarView = ((CalendarioActivity) getActivity()).calendar;

        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.shouldDrawIndicatorsBelowSelectedDays(true);

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date day) {
                int p = events.size() - 1;

                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i) instanceof EventBase) {
                        if (((EventBase) events.get(i)).getStartTime() >= day.getTime()) {
                            p = i;
                            break;
                        }
                    }
                }

                smoothScroller.setTargetPosition(p);
                layout.startSmoothScroll(smoothScroller);
            }

            @Override
            public void onMonthScroll(Date month) {
                setTitle(month.getTime());

                int p = events.size() - 1;

                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i) instanceof Month) {
                        if (((Month) events.get(i)).getTime() >= month.getTime()) {
                            p = i;
                            break;
                        }
                    }
                }

                smoothScroller.setTargetPosition(p);
                layout.startSmoothScroll(smoothScroller);
            }
        });

        displayEvents();
        scrollToToday(false);

        /*List<DaySection> sections = new ArrayList<>();

        for (CalendarBase event : events) {
            if (event instanceof Day) {
                sections.add(new DaySection((Day) event));
            } else {
                sections.add(new DaySection((EventBaseTEST) event));
            }
        }

        MultipleItemQuickAdapter multipleItemQuickAdapter = new MultipleItemQuickAdapter(R.layout.header_day, sections);*/

    }

    private void loadData() {
        Box<EventUser> eventUserBox = App.getBox().boxFor(EventUser.class);
        Box<EventJournal> eventJournalBox = App.getBox().boxFor(EventJournal.class);
        Box<EventImage> eventImageBox = App.getBox().boxFor(EventImage.class);
        Box<EventSimple> eventSimpleBox = App.getBox().boxFor(EventSimple.class);
        Box<Month> monthBox = App.getBox().boxFor(Month.class);

        events = new ArrayList<>();

        events.addAll(eventUserBox.query().build().find());
        events.addAll(eventJournalBox.query().build().find());
        events.addAll(eventImageBox.query().build().find());
        events.addAll(eventSimpleBox.query().build().find());
        events.addAll(monthBox.query().build().find());

        Collections.sort(events, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));

        for (int i = 0; i < events.size() - 1; i++) {

            CalendarBase e1 = events.get(i);
            CalendarBase e2 = events.get(i + 1);

            Calendar start = Calendar.getInstance();
            start.setTime(e1.getDate());

            if (e1 instanceof EventBase) {
                if (((EventBase) e1).isRanged()) {
                    start.setTime(new Date(((EventBase) e1).getEndTime()));
                }
            }

            Calendar end = Calendar.getInstance();
            end.setTime(e2.getDate());

            if (start.getTimeInMillis() < end.getTimeInMillis()) {
                if (start.get(Calendar.DAY_OF_MONTH) < end.get(Calendar.DAY_OF_MONTH) - 1) {

                    if (!(e1 instanceof Month)) {
                        start.add(Calendar.DAY_OF_MONTH, 1);
                    }

                    end.add(Calendar.DAY_OF_MONTH, -1);

                    events.add(i + 1, new Day(start.getTime(), end.getTime()));
                    i++;
                } else {
                    if (e2 instanceof Month) {
                        start.add(Calendar.DAY_OF_MONTH, 1);
                        end.add(Calendar.DAY_OF_MONTH, -1);
                        if (start.get(Calendar.MONTH) == end.get(Calendar.MONTH)) {
                            events.add(i + 1, new Day(start.getTime(), end.getTime()));
                            i++;
                        }
                    }
                }
            }
        }

        /*CalendarBase lastEvent = events.get(events.size() - 1);

        Calendar firstDate = Calendar.getInstance();
        firstDate.setTime(lastEvent.getDate());

        if (!(lastEvent instanceof Month)) {
            firstDate.add(Calendar.DAY_OF_MONTH, 1);
        }

        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(lastEvent.getDate());
        lastDate.add(Calendar.MONTH, 1);
        lastDate.set(Calendar.DAY_OF_MONTH, 0);

        events.add(new Day(firstDate.getTime(), lastDate.getTime()));*/
    }

    private void displayEvents() {
        //Date date = new Date();

        //setTitle(date.getTime());

        calendarView.removeAllEvents();
        //calendarView.setCurrentDate(date);

        loadData();

        for (int i = 0; i < events.size(); i++) {
            if (events.get(i) instanceof EventBase) {
                EventBase event = (EventBase) events.get(i);

                if (event.isRanged()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(event.getStartTime());

                    while (calendar.getTimeInMillis() <= event.getEndTime()) {
                        calendarView.addEvent(new Event(event.getColor(), calendar.getTimeInMillis()));
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    }

                } else {
                    calendarView.addEvent(event);
                }
            }
        }

        if (adapter == null) {
            adapter = new EventosAdapter(getContext(), events, true);
        } else {
            adapter.update(events);
        }
    }

    private void scrollToToday(boolean smooth) {
        int p = events.size() - 1;

        for (int i = 0; i < events.size(); i++) {
            if (events.get(i) instanceof Month) {
                if (((Month) events.get(i)).getTime() <= new Date().getTime()) {
                    p = i;
                }
            }
        }

        if (smooth) {
            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {
                @Override
                protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_START;
                }
            };

            smoothScroller.setTargetPosition(p);
            layout.startSmoothScroll(smoothScroller);

        } else {
            layout.scrollToPosition(p);
        }

        calendarView.setCurrentDate(events.get(p).getDate());
        setTitle(events.get(p).getDate().getTime());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_calendario);

        recyclerView.post(() -> {
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.setLayoutManager(layout);
            recyclerView.setAdapter(adapter);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int i = layout.findFirstVisibleItemPosition();

                    if (events.get(i) instanceof Month) {
                        calendarView.setCurrentDate(events.get(i).getDate());
                        setTitle(((Month) events.get(i)).getTime());
                    }
                }
            });
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_add_calendar);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EventCreateActivity.class);
            intent.putExtra("TYPE", EVENT);
            startActivity(intent);
        });
    }

    private void setTitle(long date) {
        ((CalendarioActivity) getActivity()).setTitle(new SimpleDateFormat("MMM ― yyyy", Locale.getDefault()).format(date));
    }

    @Override
    public void onUpdate(int pg) {
        if (pg == PG_CALENDARIO || pg == UPDATE_REQUEST) {
            displayEvents();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Client.get().addOnUpdateListener(this);
        calendarView = ((CalendarioActivity) Objects.requireNonNull(getActivity())).calendar;
    }

    @Override
    public void onResume() {
        super.onResume();
        Client.get().addOnUpdateListener(this);
        calendarView = ((CalendarioActivity) Objects.requireNonNull(getActivity())).calendar;
    }

    @Override
    public void onPause() {
        super.onPause();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Client.get().removeOnUpdateListener(this);
    }

    @Override
    public void onScrollRequest() {
        scrollToToday(true);
    }

}
