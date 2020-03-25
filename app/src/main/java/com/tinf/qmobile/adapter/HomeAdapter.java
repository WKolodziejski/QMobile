package com.tinf.qmobile.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.holder.calendar.CalendarViewHolder;
import com.tinf.qmobile.holder.calendar.horizontal.EmptyViewHolder;
import com.tinf.qmobile.holder.calendar.horizontal.EventJournalHorizontalViewHolder;
import com.tinf.qmobile.holder.calendar.horizontal.EventSimpleHorizontalViewHolder;
import com.tinf.qmobile.holder.calendar.horizontal.EventUserHorizontalViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.EventJournalVerticalViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.EventSimpleVerticalViewHolder;
import com.tinf.qmobile.holder.calendar.vertical.EventUserVerticalViewHolder;
import com.tinf.qmobile.model.calendar.Empty;
import com.tinf.qmobile.model.calendar.EventSimple;
import com.tinf.qmobile.model.calendar.EventSimple_;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.calendar.EventUser_;
import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.model.calendar.EventBase;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.journal.Journal_;
import com.tinf.qmobile.model.matter.Matter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;

import static com.tinf.qmobile.model.calendar.CalendarBase.ViewType.EMPTY;
import static com.tinf.qmobile.model.calendar.CalendarBase.ViewType.JOURNAL;
import static com.tinf.qmobile.model.calendar.CalendarBase.ViewType.SIMPLE;
import static com.tinf.qmobile.model.calendar.CalendarBase.ViewType.USER;

public class HomeAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private List<EventBase> events;
    private Context context;
    private DataSubscription sub1, sub2, sub3, sub4;

    public HomeAdapter(Context context) {
        this.context = context;

        BoxStore boxStore = DataBase.get().getBoxStore();

        events = getList();

        DataObserver observer = data -> {
            List<EventBase> updated = getList();

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
                    return events.get(o).id == updated.get(n).id;

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

        sub1 = boxStore.subscribe(EventUser.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        sub2 = boxStore.subscribe(Journal.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        sub3 = boxStore.subscribe(Matter.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);

        sub4 = boxStore.subscribe(EventSimple.class)
                .onlyChanges()
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(observer);
    }

    private List<EventBase> getList() {

        List<EventBase> list = new ArrayList<>();

        Box<EventUser> eventUserBox = DataBase.get().getBoxStore().boxFor(EventUser.class);
        Box<Journal> eventJournalBox = DataBase.get().getBoxStore().boxFor(Journal.class);
        Box<EventSimple> eventSimpleBox = DataBase.get().getBoxStore().boxFor(EventSimple.class);

        Calendar current = Calendar.getInstance();
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);

        list.addAll(eventUserBox.query().greater(EventUser_.startTime, current.getTimeInMillis() - 1).build().find());
        list.addAll(eventJournalBox.query().greater(Journal_.startTime, current.getTimeInMillis() - 1).build().find());
        list.addAll(eventSimpleBox.query().greater(EventSimple_.startTime, current.getTimeInMillis() - 1).build().find());

        Collections.sort(list, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));

        if (list.isEmpty())
            list.add(new Empty());

        return list;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case JOURNAL:
                return new EventJournalHorizontalViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_event_journal_h, parent, false));

            case SIMPLE:
                return new EventSimpleHorizontalViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_event_simple_h, parent, false));

            case USER:
                return new EventUserHorizontalViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_event_user_h, parent, false));

            case EMPTY:
                return new EmptyViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.calendar_empty, parent, false));
        }
        return null;
    }

    @Override
    public int getItemViewType(int i) {
        //if (events.isEmpty())
            //return EMPTY;
        //else
            return events.get(i).getItemType();
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int i) {
        //if (!events.isEmpty())
            holder.bind(events.get(i), context);
    }

    @Override
    public int getItemCount() {
        //if (events.isEmpty())
            //return 1;
        //else
            return events.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        sub1.cancel();
        sub2.cancel();
        sub3.cancel();
        sub4.cancel();
    }

}
