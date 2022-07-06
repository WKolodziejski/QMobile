package com.tinf.qmobile.database;

import android.util.Log;

import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.model.calendar.EventBase;
import com.tinf.qmobile.model.calendar.EventSimple;
import com.tinf.qmobile.model.calendar.EventSimple_;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.calendar.EventUser_;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.journal.Journal_;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.utility.EventsUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import io.objectbox.Box;
import io.objectbox.reactive.DataSubscription;

public class EventsDataProvider extends BaseDataProvider<EventBase> {
    private DataSubscription sub1;
    private DataSubscription sub2;
    private DataSubscription sub3;
    private DataSubscription sub4;

    @Override
    protected synchronized List<EventBase> buildList() {
        List<EventBase> list = new ArrayList<>();

        Box<EventUser> eventUserBox = DataBase.get().getBoxStore().boxFor(EventUser.class);
        Box<Journal> eventJournalBox = DataBase.get().getBoxStore().boxFor(Journal.class);
        Box<EventSimple> eventSimpleBox = DataBase.get().getBoxStore().boxFor(EventSimple.class);

        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        if (EventsUtils.getEventsLength() > 0) {
            Calendar end = Calendar.getInstance();
            end.add(Calendar.DAY_OF_MONTH, EventsUtils.getEventsLength() * 7);
            end.set(Calendar.HOUR_OF_DAY, 23);
            end.set(Calendar.MINUTE, 59);
            end.set(Calendar.SECOND, 59);
            end.set(Calendar.MILLISECOND, 999);

            list.addAll(eventUserBox
                    .query()
                    .greaterOrEqual(EventUser_.startTime, start.getTimeInMillis())
                    .lessOrEqual(EventUser_.startTime, end.getTimeInMillis())
                    .build()
                    .find());
            list.addAll(eventJournalBox
                    .query()
                    .greaterOrEqual(Journal_.startTime, start.getTimeInMillis())
                    .lessOrEqual(Journal_.startTime, end.getTimeInMillis())
                    .build()
                    .find());
            list.addAll(eventSimpleBox
                    .query()
                    .greaterOrEqual(EventSimple_.startTime, start.getTimeInMillis())
                    .lessOrEqual(EventSimple_.startTime, end.getTimeInMillis())
                    .build()
                    .find());
        } else {
            list.addAll(eventUserBox
                    .query()
                    .greaterOrEqual(EventUser_.startTime, start.getTimeInMillis())
                    .build()
                    .find());
            list.addAll(eventJournalBox
                    .query()
                    .greaterOrEqual(Journal_.startTime, start.getTimeInMillis())
                    .build()
                    .find());
            list.addAll(eventSimpleBox
                    .query()
                    .greaterOrEqual(EventSimple_.startTime, start.getTimeInMillis())
                    .build()
                    .find());
        }

        Collections.sort(list, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));

//        if (list.isEmpty())
//            list.add(new Empty());

        return list;
    }

    @Override
    protected void open() {
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
    }

    @Override
    protected void close() {
        super.close();
        sub1.cancel();
        sub2.cancel();
        sub3.cancel();
        sub4.cancel();
    }

}
