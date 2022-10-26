package com.tinf.qmobile.adapter;

import static com.tinf.qmobile.model.ViewType.CLASS;
import static com.tinf.qmobile.model.ViewType.DAY;
import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.HEADER;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MONTH;
import static com.tinf.qmobile.model.ViewType.SIMPLE;
import static com.tinf.qmobile.model.ViewType.USER;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.kodmap.library.kmrecyclerviewstickyheader.KmStickyListener;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.holder.calendar.horizontal.EmptyViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.DayViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.EventClazzVerticalViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.EventJournalVerticalViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.EventSimpleVerticalViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.EventUserVerticalViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.CalendarHeaderViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.MonthViewHolder;
import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.model.calendar.Day;
import com.tinf.qmobile.model.calendar.EventBase;
import com.tinf.qmobile.model.calendar.EventSimple;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.calendar.Header;
import com.tinf.qmobile.model.calendar.Month;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Clazz;
import com.tinf.qmobile.model.matter.Matter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Months;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.objectbox.Box;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> implements KmStickyListener {
    private final Context context;
    private final AsyncListDiffer<CalendarBase> list;
    private final CompactCalendarView calendar;
    private final DataSubscription sub1;
    private final DataSubscription sub2;
    private final DataSubscription sub3;
    private final DataSubscription sub4;
    private final DataSubscription sub5;
    private final OnCalendar onCalendar;
    private final Handler handler;

    public CalendarAdapter(Context context, CompactCalendarView calendar, OnCalendar onCalendar) {
        this.context = context;
        this.calendar = calendar;
        this.onCalendar = onCalendar;
        this.handler = new Handler(Looper.getMainLooper());
        this.list = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<CalendarBase>() {
            @Override
            public boolean areItemsTheSame(@NonNull CalendarBase oldItem, @NonNull CalendarBase newItem) {
                return oldItem.getId() == newItem.getId() && oldItem.getItemType() == newItem.getItemType();
            }

            @Override
            public boolean areContentsTheSame(@NonNull CalendarBase oldItem, @NonNull CalendarBase newItem) {
                return oldItem.isSame(newItem);
            }
        });

        updateList(true);

        DataObserver observer = data -> updateList(false);

        sub1 = DataBase.get().getBoxStore().subscribe(EventUser.class)
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);

        sub2 = DataBase.get().getBoxStore().subscribe(Journal.class)
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);

        sub3 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);

        sub4 = DataBase.get().getBoxStore().subscribe(EventSimple.class)
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);

        sub5 = DataBase.get().getBoxStore().subscribe(Clazz.class)
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);
    }

    private void updateList(boolean scroll) {
        DataBase.get().execute(() -> {
            List<CalendarBase> list = buildList();

            handler.post(() -> {
                this.list.submitList(list);

                if (scroll)
                    onCalendar.scrollToToday();
            });
        });
    }

    private List<CalendarBase> buildList() {
        Map<LocalDate, List<CalendarBase>> map = new TreeMap<>();

        LocalDate minDate = new LocalDate().minusYears(5).toDateTimeAtStartOfDay().dayOfMonth().withMinimumValue().toLocalDate();
        LocalDate maxDate = new LocalDate().plusYears(5).toDateTimeAtStartOfDay().dayOfMonth().withMaximumValue().toLocalDate();
        LocalDate monthCounter = minDate;

        for (int i = 0; i < Months.monthsBetween(minDate, maxDate).getMonths(); i++) {
            Month month = new Month(monthCounter.toDate().getTime());
            //Log.d(month.getMonth(), String.valueOf(monthCounter.toDate().getTime()));

            List<CalendarBase> list = map.get(month.getHashKey());

            if (list == null) {
                list = new ArrayList<>();
                map.put(month.getHashKey(), list);
            }

            list.add(0, month);

            DateTime startDay = monthCounter.dayOfMonth().withMinimumValue().toDateTimeAtStartOfDay();
            LocalDate week = startDay.dayOfWeek().withMaximumValue().toLocalDate();//.minusDays(1);

            while (week.compareTo(startDay.dayOfMonth().withMaximumValue().toLocalDate()) < 0) {
                Day day = new Day(week.toDate(), week.plusDays(6).toDate());
                //Log.d(day.getDayPeriod(), String.valueOf(week.toDate().getTime()));

                List<CalendarBase> list2 = map.get(day.getHashKey());

                if (list2 == null) {
                    list2 = new ArrayList<>();
                    map.put(day.getHashKey(), list2);
                }

                list2.add(day);

                week = week.plusWeeks(1);
            }

            monthCounter = monthCounter.plusMonths(1);
        }

        Box<EventUser> eventUserBox = DataBase.get().getBoxStore().boxFor(EventUser.class);
        Box<Journal> eventJournalBox = DataBase.get().getBoxStore().boxFor(Journal.class);
        Box<EventSimple> eventSimpleBox = DataBase.get().getBoxStore().boxFor(EventSimple.class);
        Box<Clazz> clazzBox = DataBase.get().getBoxStore().boxFor(Clazz.class);

        for (EventBase e : eventUserBox.query().build().find()) {
            List<CalendarBase> list = map.get(e.getHashKey());

            if (list == null) {
                list = new ArrayList<>();
                map.put(e.getHashKey(), list);
            }

            list.add(e);
        }

        for (EventBase e : eventJournalBox.query().build().find()) {
            List<CalendarBase> list = map.get(e.getHashKey());

            if (list == null) {
                list = new ArrayList<>();
                map.put(e.getHashKey(), list);
            }

            list.add(e);
        }

        for (EventBase e : eventSimpleBox.query().build().find()) {
            List<CalendarBase> list = map.get(e.getHashKey());

            if (list == null) {
                list = new ArrayList<>();
                map.put(e.getHashKey(), list);
            }

            list.add(e);
        }

        for (EventBase e : clazzBox.query().build().find()) {
            List<CalendarBase> list = map.get(e.getHashKey());

            if (list == null) {
                list = new ArrayList<>();
                map.put(e.getHashKey(), list);
            }

            list.add(e);
        }

        calendar.post(calendar::removeAllEvents);

        List<CalendarBase> ret = new ArrayList<>();
        List<Event> toAdd = new ArrayList<>();

        for (LocalDate key : map.keySet()) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(key.toDate());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            List<CalendarBase> list = map.get(key);
            Collections.sort(list, (t1, t2) -> t1.getHashKey().compareTo(t2.getHashKey()));

            boolean hasHeader = false;

            for (int i = 0; i < list.size(); i++) {
                CalendarBase cb = list.get(i);

                if (cb instanceof EventBase) {
                    if (!hasHeader) {
                        hasHeader = true;
                        list.add(i, new Header(cal.getTimeInMillis()));
                        i++;
                        ((EventBase) cb).isHeader = true;
                    }

                    toAdd.add((Event) cb);
                }
            }

            ret.addAll(list);
        }

        calendar.post(() -> calendar.addEvents(toAdd));

        if (ret.isEmpty())
            ret.add(new Empty());

        return ret;
    }

    public List<CalendarBase> getList() {
        return list.getCurrentList();
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

            case CLASS:
                return new EventClazzVerticalViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_event_clazz_v, parent, false));

            case MONTH:
                return new MonthViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_header_month, parent, false));

            case DAY:
                return new DayViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_header_day_range, parent, false));

            case HEADER:
                return new CalendarHeaderViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.header_empty, parent, false));

            case EMPTY:
                return new EmptyViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_event_empty, parent, false));
        }

        return null;
    }

    @Override
    public int getItemViewType(int i) {
        return list.getCurrentList().get(i).getItemType();
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int i) {
        holder.bind(list.getCurrentList().get(i), context);
    }

    @Override
    public int getItemCount() {
        return list.getCurrentList().size();
    }

    @Override
    public Integer getHeaderPositionForItem(Integer i) {
        CalendarBase e = list.getCurrentList().get(i);

        if (e instanceof Month || e instanceof Day)
            return i;

        while (!(e instanceof Header) && i > 0)
            e = list.getCurrentList().get(--i);

        return i;
    }

    @Override
    public Integer getHeaderLayout(Integer i) {
        if (list.getCurrentList().get(i) instanceof Header)
            return R.layout.calendar_header_day_single;
        else
            return R.layout.header_empty;
    }

    @Override
    public void bindHeaderData(View header, Integer i) {
        if (list.getCurrentList().get(i) instanceof Header) {
            Header h = (Header) list.getCurrentList().get(i);

            TextView n = header.findViewById(R.id.number);
            TextView w = header.findViewById(R.id.day);

            n.setText(h.getDayString());
            w.setText(h.getWeekString());
        }
    }

    @Override
    public Boolean isHeader(Integer i) {
        CalendarBase e = list.getCurrentList().get(i);

        if (i >= 0)
            return e instanceof Header || e instanceof Day || e instanceof Month;

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

    public interface OnCalendar {
        void scrollToToday();
    }

}
