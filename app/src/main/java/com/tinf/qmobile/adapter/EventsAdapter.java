package com.tinf.qmobile.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kodmap.library.kmrecyclerviewstickyheader.KmStickyListener;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.holder.calendar.horizontal.EmptyViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.DayViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.EventJournalVerticalViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.EventSimpleVerticalViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.EventUserVerticalViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.HeaderViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.MonthViewHolder;
import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.calendar.Day;
import com.tinf.qmobile.model.calendar.EventSimple;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.calendar.Header;
import com.tinf.qmobile.model.calendar.Month;
import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.model.calendar.EventBase;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Matter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

import static com.tinf.qmobile.model.ViewType.DAY;
import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.HEADER;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MONTH;
import static com.tinf.qmobile.model.ViewType.SIMPLE;
import static com.tinf.qmobile.model.ViewType.USER;

public class EventsAdapter extends RecyclerView.Adapter<CalendarViewHolder> implements KmStickyListener {
    private List<CalendarBase> events;
    private Context context;
    private DataSubscription sub1, sub2, sub3, sub4, sub5;

    public EventsAdapter(Context context) {
        this.context = context;

        events = getList();

        DataObserver observer = data -> {
            List<CalendarBase> updated = getList();

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return events.size();
                }

                @Override
                public int getNewListSize() {
                    return updated.size();
                }

                @Override
                public boolean areItemsTheSame(int o, int n) {
                    CalendarBase oldC = events.get(o);
                    CalendarBase newC = updated.get(n);

                    if (oldC instanceof EventBase && newC instanceof EventBase)
                        return ((EventBase) oldC).id == ((EventBase) newC).id;

                    else if (oldC instanceof Month && newC instanceof Month)
                        return ((Month) oldC).id == ((Month) newC).id;

                    else if (oldC instanceof Header && newC instanceof Header)
                        return oldC.getDate().equals(newC.getDate());

                    else if (oldC instanceof Day && newC instanceof Day)
                        return oldC.getDate().equals(newC.getDate());

                    else return oldC instanceof Empty && newC instanceof Empty;
                }

                @Override
                public boolean areContentsTheSame(int o, int n) {
                    return events.get(o).equals(updated.get(n));
                }

            }, true);

            events.clear();
            events.addAll(updated);
            result.dispatchUpdatesTo(this);
        };

        sub1 = DataBase.get().getBoxStore().subscribe(EventUser.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        sub2 = DataBase.get().getBoxStore().subscribe(Journal.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        sub3 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        sub4 = DataBase.get().getBoxStore().subscribe(EventSimple.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        sub5 = DataBase.get().getBoxStore().subscribe(Month.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);
    }

    private List<CalendarBase> getList() {

        List<CalendarBase> list = new ArrayList<>();

        Box<EventUser> eventUserBox = DataBase.get().getBoxStore().boxFor(EventUser.class);
        Box<Journal> eventJournalBox = DataBase.get().getBoxStore().boxFor(Journal.class);
        Box<EventSimple> eventSimpleBox = DataBase.get().getBoxStore().boxFor(EventSimple.class);
        Box<Month> monthBox = DataBase.get().getBoxStore().boxFor(Month.class);

        list.addAll(eventUserBox.query().build().find());
        list.addAll(eventJournalBox.query().build().find());
        list.addAll(eventSimpleBox.query().build().find());
        list.addAll(monthBox.query().build().find());

        if (list.isEmpty()) {
            list.add(new Empty());
            return list;
        }

        Collections.sort(list, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));

        for (int i = 0; i < list.size(); i++) {
            CalendarBase e1 = list.get(i);

            if (!(e1 instanceof Day) && !(e1 instanceof Month)) {

                int j = i + 1;

                if (j < list.size() - 1) {

                    CalendarBase e2 = list.get(j);

                    while (e1.getDay() == e2.getDay() && e1.getYear() == e2.getYear()) {
                        e2 = list.get(++j);
                    }
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(e1.getDate());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                list.add(i, new Header(cal.getTimeInMillis()));

                i = j;
            }
        }

        for (int i = 0; i < list.size() - 1; i++) {

            CalendarBase e1 = list.get(i);
            CalendarBase e2 = list.get(i + 1);

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

                    list.add(i + 1, new Day(start.getTime(), end.getTime()));
                    i++;
                } else {
                    if (e2 instanceof Month) {
                        start.add(Calendar.DAY_OF_MONTH, 1);
                        end.add(Calendar.DAY_OF_MONTH, -1);
                        if (start.get(Calendar.MONTH) == end.get(Calendar.MONTH)) {
                            list.add(i + 1, new Day(start.getTime(), end.getTime()));
                            i++;
                        }
                    }
                }
            }
        }

        return list;
    }

    public List<CalendarBase> getEvents() {
        return events;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case JOURNAL:
                return new EventJournalVerticalViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_event_journal_v, parent, false));

            case SIMPLE:
                return new EventSimpleVerticalViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_event_simple_v, parent, false));

            case USER:
                return new EventUserVerticalViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_event_user_v, parent, false));

            case MONTH:
                return new MonthViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_header_month, parent, false));

            case DAY:
                return new DayViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_header_day_range, parent, false));

            case HEADER:
                return new HeaderViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_header_day_single, parent, false));

            case EMPTY:
                return new EmptyViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_event_empty, parent, false));
        }
        return null;
    }

    @Override
    public int getItemViewType(int i) {
        return events.get(i).getItemType();
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int i) {
        holder.bind(events.get(i), context);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public Integer getHeaderPositionForItem(Integer i) {
        CalendarBase e = events.get(i);

        if (e instanceof Month || e instanceof Day)
            return i;

        while (!(e instanceof Header) && i > 0) {
            e = events.get(--i);
        }

        return i;
    }

    @Override
    public Integer getHeaderLayout(Integer i) {
        if (events.get(i) instanceof Header)
            return R.layout.calendar_header_day_single;
        else
            return R.layout.calendar_header_empty;
    }

    @Override
    public void bindHeaderData(View header, Integer i) {
        if (events.get(i) instanceof Header) {
            TextView n = header.findViewById(R.id.calendar_header_simple_day_number);
            TextView w = header.findViewById(R.id.calendar_header_simple_day_week);

            n.setText(((Header) events.get(i)).getDayString());
            w.setText(((Header) events.get(i)).getWeekString());
        }
    }

    @Override
    public Boolean isHeader(Integer i) {
        if (i >= 0 && i < events.size())
            return events.get(i) instanceof Header || events.get(i) instanceof Day || events.get(i) instanceof Month;
        else return false;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        sub1.cancel();
        sub2.cancel();
        sub3.cancel();
        sub4.cancel();
        sub5.cancel();
    }

}
