package com.tinf.qmobile.database;

import android.os.Build;

import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.model.calendar.Day;
import com.tinf.qmobile.model.calendar.Event;
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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.objectbox.Box;
import io.objectbox.reactive.DataSubscription;

public class CalendarDataProvider extends BaseDataProvider<CalendarBase> {
  private DataSubscription sub1;
  private DataSubscription sub2;
  private DataSubscription sub3;
  private DataSubscription sub4;
  private DataSubscription sub5;
  private List<Event> eventsList;

  public CalendarDataProvider() {
    super();
    this.eventsList = new ArrayList<>();
  }

  @Override
  protected synchronized List<CalendarBase> buildList() {
    Map<LocalDate, List<CalendarBase>> map = new TreeMap<>();

    LocalDate minDate = new LocalDate().minusYears(5)
                                       .toDateTimeAtStartOfDay()
                                       .dayOfMonth()
                                       .withMinimumValue()
                                       .toLocalDate();
    LocalDate maxDate = new LocalDate().plusYears(5)
                                       .toDateTimeAtStartOfDay()
                                       .dayOfMonth()
                                       .withMaximumValue()
                                       .toLocalDate();
    LocalDate monthCounter = minDate;

    for (int i = 0; i < Months.monthsBetween(minDate, maxDate)
                              .getMonths(); i++) {
      Month month = new Month(monthCounter.toDate()
                                          .getTime());

      List<CalendarBase> list = map.get(month.getHashKey());

      if (list == null) {
        list = new ArrayList<>();
        map.put(month.getHashKey(), list);
      }

      list.add(0, month);

      DateTime startDay = monthCounter.dayOfMonth()
                                      .withMinimumValue()
                                      .toDateTimeAtStartOfDay();
      LocalDate week = startDay.dayOfWeek()
                               .withMaximumValue()
                               .toLocalDate();

      while (week.compareTo(startDay.dayOfMonth()
                                    .withMaximumValue()
                                    .toLocalDate()) < 0) {
        Day day = new Day(week.toDate(), week.plusDays(6)
                                             .toDate());

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

    Box<EventUser> eventUserBox = DataBase.get()
                                          .getBoxStore()
                                          .boxFor(EventUser.class);
    Box<Journal> eventJournalBox = DataBase.get()
                                           .getBoxStore()
                                           .boxFor(Journal.class);
    Box<EventSimple> eventSimpleBox = DataBase.get()
                                              .getBoxStore()
                                              .boxFor(EventSimple.class);
    Box<Clazz> clazzBox = DataBase.get()
                                  .getBoxStore()
                                  .boxFor(Clazz.class);

    for (Event e : eventUserBox.query()
                               .build()
                               .find()) {
      List<CalendarBase> list = map.get(e.getHashKey());

      if (list == null) {
        list = new ArrayList<>();
        map.put(e.getHashKey(), list);
      }

      list.add(e);
    }

    for (Event e : eventJournalBox.query()
                                  .build()
                                  .find()) {
      List<CalendarBase> list = map.get(e.getHashKey());

      if (list == null) {
        list = new ArrayList<>();
        map.put(e.getHashKey(), list);
      }

      list.add(e);
    }

    for (Event e : eventSimpleBox.query()
                                 .build()
                                 .find()) {
      List<CalendarBase> list = map.get(e.getHashKey());

      if (list == null) {
        list = new ArrayList<>();
        map.put(e.getHashKey(), list);
      }

      list.add(e);
    }

    for (Event e : clazzBox.query()
                           .build()
                           .find()) {
      List<CalendarBase> list = map.get(e.getHashKey());

      if (list == null) {
        list = new ArrayList<>();
        map.put(e.getHashKey(), list);
      }

      list.add(e);
    }

    List<CalendarBase> ret = new ArrayList<>();
    eventsList = new ArrayList<>();

    for (LocalDate key : map.keySet()) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(key.toDate());
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);

      List<CalendarBase> list = map.get(key);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Collections.sort(list, Comparator.comparing(CalendarBase::getDate));
      } else {
        Collections.sort(list, (t1, t2) -> t1.getDate()
                                             .compareTo(t2.getDate()));
      }

      boolean hasHeader = false;

      for (int i = 0; i < list.size(); i++) {
        CalendarBase cb = list.get(i);

        if (cb instanceof Event) {
          if (!hasHeader) {
            hasHeader = true;
            list.add(i, new Header(cal.getTimeInMillis()));
            i++;
            ((Event) cb).setHeader(true);
          }

          eventsList.add((Event) cb);
        }
      }

      ret.addAll(list);
    }

    if (ret.isEmpty())
      ret.add(new Empty());

    return ret;
  }

  public List<Event> getEvents() {
    return eventsList;
  }

  @Override
  protected void open() {
    sub1 = DataBase.get()
                   .getBoxStore()
                   .subscribe(EventUser.class)
                   .onlyChanges()
                   .onError(Throwable::printStackTrace)
                   .observer(observer);

    sub2 = DataBase.get()
                   .getBoxStore()
                   .subscribe(Journal.class)
                   .onlyChanges()
                   .onError(Throwable::printStackTrace)
                   .observer(observer);

    sub3 = DataBase.get()
                   .getBoxStore()
                   .subscribe(Matter.class)
                   .onlyChanges()
                   .onError(Throwable::printStackTrace)
                   .observer(observer);

    sub4 = DataBase.get()
                   .getBoxStore()
                   .subscribe(EventSimple.class)
                   .onlyChanges()
                   .onError(Throwable::printStackTrace)
                   .observer(observer);

    sub5 = DataBase.get()
                   .getBoxStore()
                   .subscribe(Clazz.class)
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
    sub5.cancel();
  }

}
