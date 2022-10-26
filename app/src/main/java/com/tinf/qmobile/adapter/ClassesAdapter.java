package com.tinf.qmobile.adapter;

import static com.tinf.qmobile.model.ViewType.CLASS;
import static com.tinf.qmobile.model.ViewType.DAY;
import static com.tinf.qmobile.model.ViewType.EMPTY;
import static com.tinf.qmobile.model.ViewType.HEADER;
import static com.tinf.qmobile.model.ViewType.MONTH;

import android.content.Context;
import android.os.Bundle;
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

import com.kodmap.library.kmrecyclerviewstickyheader.KmStickyListener;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.holder.calendar.horizontal.EmptyViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.DayViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.EventClazzVerticalViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.CalendarHeaderViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.MonthViewHolder;
import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.model.calendar.Day;
import com.tinf.qmobile.model.calendar.EventBase;
import com.tinf.qmobile.model.calendar.Header;
import com.tinf.qmobile.model.calendar.Month;
import com.tinf.qmobile.model.matter.Clazz;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Period;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Months;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

public class ClassesAdapter extends RecyclerView.Adapter<CalendarViewHolder> implements KmStickyListener {
    private final Context context;
    private final AsyncListDiffer<CalendarBase> list;
    private final DataSubscription sub1;
    private final DataSubscription sub2;
    private final Handler handler;

    public ClassesAdapter(Context context, Bundle bundle) {
        this.context = context;
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

        updateList(bundle);

        DataObserver observer = data -> updateList(bundle);

        sub1 = DataBase.get().getBoxStore().subscribe(Matter.class)
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);

        sub2 = DataBase.get().getBoxStore().subscribe(Clazz.class)
                .onlyChanges()
                .onError(Throwable::printStackTrace)
                .observer(observer);
    }

    private void updateList(Bundle bundle) {
        DataBase.get().execute(() -> {
            List<CalendarBase> list = getList(bundle);

            handler.post(() -> this.list.submitList(list));
        });
    }

    private List<CalendarBase> getList(Bundle bundle) {
        Map<LocalDate, List<CalendarBase>> map = new TreeMap<>();
        List<CalendarBase> ret = new ArrayList<>();
        List<Clazz> classes = new ArrayList<>();

        Matter matter = DataBase.get().getBoxStore()
                .boxFor(Matter.class)
                .get(bundle.getLong("ID"));

        for (int i = 0; i < matter.periods.size(); i++) {
            Period p = matter.periods.get(i);

            if (!p.classes.isEmpty()) {
                List<Clazz> cls = p.classes;
                classes.addAll(cls);
            }
        }

        EventBase firstClazz = null;
        EventBase lastClazz = null;

        for (EventBase e : classes) {
            List<CalendarBase> list = map.get(e.getHashKey());

            if (list == null) {
                list = new ArrayList<>();
                map.put(e.getHashKey(), list);
            }

            list.add(e);

            if (firstClazz == null)
                firstClazz = e;

            if (e.getStartTime() < firstClazz.getStartTime())
                firstClazz = e;

            if (lastClazz == null)
                lastClazz = e;

            if (e.getStartTime() > lastClazz.getStartTime())
                lastClazz = e;
        }

        if (firstClazz == null) {
            ret.add(new Empty());
            return ret;
        }

        LocalDate minDate = new LocalDate(firstClazz.getStartTime()).toDateTimeAtStartOfDay().dayOfMonth().withMinimumValue().toLocalDate();
        LocalDate maxDate = new LocalDate(lastClazz.getStartTime()).toDateTimeAtStartOfDay().dayOfMonth().withMaximumValue().toLocalDate();

        if (Months.monthsBetween(minDate, maxDate).getMonths() == 0) {
            maxDate = maxDate.plusMonths(1);
        }

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
                }
            }

            ret.addAll(list);
        }

        if (ret.isEmpty())
            ret.add(new Empty());

        return ret;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
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
                        .inflate(R.layout.class_empty, parent, false));
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
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        sub1.cancel();
        sub2.cancel();
    }

    @Override
    public Integer getHeaderPositionForItem(Integer i) {
        CalendarBase e = list.getCurrentList().get(i);

        if (e instanceof Month || e instanceof Day)
            return i;

        while (!(e instanceof Header) && i > 0)
            e = list.getCurrentList().get(--i);

        return i < 0 ? 0 : i;
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
        if (i < 0)
            return false;

        CalendarBase e = list.getCurrentList().get(i);
        return e instanceof Header || e instanceof Day || e instanceof Month;
    }
}
