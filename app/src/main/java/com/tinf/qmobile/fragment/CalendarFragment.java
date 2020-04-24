package com.tinf.qmobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.kodmap.library.kmrecyclerviewstickyheader.KmHeaderItemDecoration;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.CalendarActivity;
import com.tinf.qmobile.activity.EventCreateActivity;
import com.tinf.qmobile.adapter.EventsAdapter;
import com.tinf.qmobile.model.calendar.Month;
import com.tinf.qmobile.model.calendar.EventBase;
import com.tinf.qmobile.network.Client;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import static com.tinf.qmobile.activity.EventCreateActivity.EVENT;

public class CalendarFragment extends Fragment implements OnUpdate {
    private CompactCalendarView calendarView;
    private LinearLayoutManager layout;
    private EventsAdapter adapter;

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

        calendarView = ((CalendarActivity) getActivity()).calendar;

        adapter = new EventsAdapter(getContext());

        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.shouldDrawIndicatorsBelowSelectedDays(true);

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date day) {

                int p = adapter.getEvents().size() - 1;

                for (int i = 0; i < adapter.getEvents().size(); i++) {
                    if (adapter.getEvents().get(i) instanceof EventBase) {
                        if (((EventBase) adapter.getEvents().get(i)).getStartTime() >= day.getTime()) {
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

                int p = adapter.getEvents().size() - 1;

                for (int i = 0; i < adapter.getEvents().size(); i++) {
                    if (adapter.getEvents().get(i) instanceof Month) {
                        if (((Month) adapter.getEvents().get(i)).getTime() >= month.getTime()) {
                            p = i;
                            break;
                        }
                    }
                }

                smoothScroller.setTargetPosition(p);
                layout.startSmoothScroll(smoothScroller);
            }
        });

        Date date = new Date();

        setTitle(date.getTime());

        calendarView.removeAllEvents();
        calendarView.setCurrentDate(date);

        for (int i = 0; i < adapter.getEvents().size(); i++) {
            if (adapter.getEvents().get(i) instanceof EventBase) {
                EventBase event = (EventBase) adapter.getEvents().get(i);

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

        scrollToToday(false);
    }

    private void scrollToToday(boolean smooth) {
        int p = adapter.getEvents().size() - 1;

        for (int i = 0; i < adapter.getEvents().size(); i++) {
            if (adapter.getEvents().get(i) instanceof Month) {
                if (((Month) adapter.getEvents().get(i)).getTime() <= new Date().getTime()) {
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

        calendarView.setCurrentDate(adapter.getEvents().get(p).getDate());
        setTitle(adapter.getEvents().get(p).getDate().getTime());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_calendario);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new KmHeaderItemDecoration(adapter));
        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int i = layout.findFirstVisibleItemPosition();

                if (adapter.getEvents().get(i) instanceof Month) {
                    calendarView.setCurrentDate(adapter.getEvents().get(i).getDate());
                    setTitle(((Month) adapter.getEvents().get(i)).getTime());
                }
            }

        });*/

        ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) view.findViewById(R.id.fab_add_calendar);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EventCreateActivity.class);
            intent.putExtra("TYPE", EVENT);
            startActivity(intent);
        });
    }

    private void setTitle(long date) {
        ((CalendarActivity) getActivity()).setTitle(new SimpleDateFormat("MMM ― yyyy", Locale.getDefault()).format(date));
    }

    @Override
    public void onStart() {
        super.onStart();
        Client.get().addOnUpdateListener(this);
        calendarView = ((CalendarActivity) Objects.requireNonNull(getActivity())).calendar;
    }

    @Override
    public void onResume() {
        super.onResume();
        Client.get().addOnUpdateListener(this);
        calendarView = ((CalendarActivity) Objects.requireNonNull(getActivity())).calendar;
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

    @Override
    public void onDateChanged() {

    }

}
