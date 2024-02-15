package com.tinf.qmobile.database;

import static com.tinf.qmobile.model.ViewType.JOURNAL_EMPTY;
import static com.tinf.qmobile.network.Client.pos;

import android.os.Build;

import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.model.journal.FooterJournal;
import com.tinf.qmobile.model.journal.HeaderMatter;
import com.tinf.qmobile.model.journal.HeaderYear;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.utility.JournalsUtils;
import com.tinf.qmobile.utility.UserUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.objectbox.reactive.DataSubscription;

public class JournalsDataProvider extends BaseDataProvider<Queryable> {
  private DataSubscription sub1;
  private DataSubscription sub2;

  @Override
  protected synchronized List<Queryable> buildList() {
    List<Queryable> list = new ArrayList<>();
    List<Matter> matters;

    if (JournalsUtils.getOrder().equals("ASC")) {
      matters = DataBase.get().getBoxStore()
              .boxFor(Matter.class)
              .query()
              .order(Matter_.title_)
              .equal(Matter_.year_, UserUtils.getYear(pos))
              .and()
              .equal(Matter_.period_, UserUtils.getPeriod(pos))
              .build()
              .find();
    } else {
      matters = DataBase.get().getBoxStore()
              .boxFor(Matter.class)
              .query()
              .orderDesc(Matter_.title_)
              .equal(Matter_.year_, UserUtils.getYear(pos))
              .and()
              .equal(Matter_.period_, UserUtils.getPeriod(pos))
              .build()
              .find();
    }

    for (int i = 0; i < matters.size(); i++) {
      Matter matter = matters.get(i);

      list.add(new HeaderMatter(matter));
      list.add(matter);

      List<Journal> items = matter.getLastJournals();

      if (items.isEmpty()) {
        list.add(new Empty(JOURNAL_EMPTY));
      } else {
        list.addAll(items);
      }

      list.add(new FooterJournal(i, matter));
    }

    if (list.isEmpty())
      list.add(new Empty());
    else if (UserUtils.getYears().length > pos)
      list.add(0, new HeaderYear(UserUtils.getYears()[pos]));

    return list;
  }

  @Override
  protected void open() {
    sub1 = DataBase.get().getBoxStore().subscribe(Matter.class)
                   .onlyChanges()
                   .onError(Throwable::printStackTrace)
                   .observer(observer);

    sub2 = DataBase.get().getBoxStore().subscribe(Journal.class)
                   .onlyChanges()
                   .onError(Throwable::printStackTrace)
                   .observer(observer);
  }

  @Override
  protected void close() {
    super.close();
    sub1.cancel();
    sub2.cancel();
  }

}
