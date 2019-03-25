package com.tinf.qmobile.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oushangfeng.pinnedsectionitemdecoration.PinnedHeaderItemDecoration;
import com.tinf.qmobile.Activity.CalendarioActivity;
import com.tinf.qmobile.Activity.Settings.EventActivity;
import com.tinf.qmobile.Adapter.Calendario.EventosAdapter;
import com.tinf.qmobile.Class.Calendario.CalendarBase;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.Event;
import com.tinf.qmobile.Class.Calendario.Event_;
import com.tinf.qmobile.Class.Calendario.Month;
import com.tinf.qmobile.Class.Calendario.Month_;
import com.tinf.qmobile.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import static com.tinf.qmobile.Network.OnResponse.PG_CALENDARIO;

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

        Calendar startOfMonth = (Calendar) Calendar.getInstance();
        Calendar endOfMonth = (Calendar) Calendar.getInstance();

        Date date = new Date();
        startOfMonth.setTime(date);
        endOfMonth.setTime(date);
        endOfMonth.add(Calendar.MONTH, 1);

        setTitle(date.getTime());

        calendarView = ((CalendarioActivity) Objects.requireNonNull(getActivity())).calendar;

        calendarView.removeAllEvents();
        calendarView.setCurrentDate(date);
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.shouldDrawIndicatorsBelowSelectedDays(true);

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date day) {
                int p = events.size() - 1;

                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i) instanceof Event) {
                        if (((Event) events.get(i)).getStartTime() >= day.getTime()) {
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

                startOfMonth.setTime(month);
                endOfMonth.setTime(month);
                endOfMonth.add(Calendar.MONTH, 1);

                int p = events.size() - 1;

                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i) instanceof Month) {
                        if (((Month) events.get(i)).getDate() >= month.getTime()) {
                            p = i;
                            break;
                        }
                    }
                }

                smoothScroller.setTargetPosition(p);
                layout.startSmoothScroll(smoothScroller);
            }
        });

        loadData();

        for (int i = 0; i < events.size(); i++) {
            if (events.get(i) instanceof Event) {
                Event event = (Event) events.get(i);

                if (event.getEndTime() != 0) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(event.getStartTime());

                    long originalTime = event.getStartTime();

                    while (calendar.getTimeInMillis() <= event.getEndTime()) {
                        calendarView.addEvent(event);
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        event.setStartTime(calendar.getTimeInMillis());
                    }

                    event.setStartTime(originalTime);
                } else {
                    calendarView.addEvent(event);
                }
            }
        }

        int p = events.size() - 1;

        for (int i = 0; i < events.size(); i++) {
            if (events.get(i) instanceof Month) {
                if (((Month) events.get(i)).getDate() >= date.getTime()) {
                    p = i;
                    break;
                }
            }
        }

        layout.scrollToPosition(p);

        adapter = new EventosAdapter(getContext(), events);


        /*List<CalendarTEST> test = new ArrayList<>();

        for (MultiItemEntity item : events) {
            if (item.getItemType() == CalendarBase.ViewType.MONTH) {
                test.add(new CalendarTEST(true, ((Month) ((MultiItemEntity) item)).getMonth()));
            } else {
                test.add(new CalendarTEST(item));
            }
        }

        adapter = new CalendarAdapterTEST(test);*/

    }



    private void loadData() {
        List<Month> months = App.getBox().boxFor(Month.class).query().order(Month_.date).build().find();

        events = new ArrayList<>();

        for (int i = 0; i < months.size(); i++) {
            int j = i == months.size() - 1 ? i : i + 1;
            events.add(months.get(i));
            if (i == months.size() - 1) {
                events.addAll(App.getBox().boxFor(Event.class).query()
                        .greater(Event_.startTime, months.get(i).getDate() - 1).build().find());
            } else {
                events.addAll(App.getBox().boxFor(Event.class).query()
                        .between(Event_.startTime, months.get(i).getDate() - 1, months.get(j).getDate() - 1).build().find());
            }
        }

        /*for (Month month : months) {
            events.add(month);
            events.addAll(month.events);
        }*/

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
                        calendarView.setCurrentDate(new Date(((Month) events.get(i)).getDate()));
                        setTitle(((Month) events.get(i)).getDate());
                    }
                }
            });
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_add_calendar);
        fab.setOnClickListener(v -> {

            startActivity(new Intent(getActivity(), EventActivity.class));

            /*Event event = new Event("TESTE", new Date().getTime(), false);
            event.setColor(Color.RED);
            App.getBox().boxFor(Event.class).put(event);

            loadData();

            int p = events.size() - 1;

            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).getStartTime() == event.getStartTime()
                        && events.get(i).getTitle().equals(event.getTitle())) {
                    p = i;
                }
            }*/

            //adapter.addItems(p, event);
        });

    }

    private void setTitle(long date) {
        ((CalendarioActivity) getActivity()).setTitle(new SimpleDateFormat("MMM ― yyyy", Locale.getDefault()).format(date));
    }

    @Override
    public void onUpdate(int pg) {
        if (pg == PG_CALENDARIO || pg == UPDATE_REQUEST) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        calendarView = ((CalendarioActivity) Objects.requireNonNull(getActivity())).calendar;
        ((CalendarioActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        calendarView = ((CalendarioActivity) Objects.requireNonNull(getActivity())).calendar;
        ((CalendarioActivity) Objects.requireNonNull(getActivity())).setOnUpdateListener(this);
    }

    @Override
    public void onScrollRequest() {}
}
