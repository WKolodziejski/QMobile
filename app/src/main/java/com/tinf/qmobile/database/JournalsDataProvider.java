package com.tinf.qmobile.database;

import static com.tinf.qmobile.model.ViewType.JOURNALEMPTY;
import static com.tinf.qmobile.network.Client.pos;

import com.tinf.qmobile.model.Empty;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.FooterJournal;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.utility.UserUtils;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.reactive.DataSubscription;

public class JournalsDataProvider extends BaseDataProvider<Queryable> {
    private DataSubscription sub1;
    private DataSubscription sub2;

    @Override
    protected synchronized List<Queryable> buildList() {
        ArrayList<Queryable> list = new ArrayList<>(
                DataBase.get().getBoxStore()
                        .boxFor(Matter.class)
                        .query()
                        .order(Matter_.title_)
                        .equal(Matter_.year_, UserUtils.getYear(pos))
                        .and()
                        .equal(Matter_.period_, UserUtils.getPeriod(pos))
                        .build()
                        .find());

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof Matter) {
                Matter matter = (Matter) list.get(i);

                List<Journal> items = matter.getLastJournals();

                if (items.isEmpty()) {
                    list.add(i + 1, new Empty(JOURNALEMPTY));
                    list.add(i + 2, new FooterJournal(i, matter));
                } else {
                    list.addAll(i + 1, items);
                    list.add(i + items.size() + 1, new FooterJournal(i, matter));
                }
            }
        }

        if (list.isEmpty())
            list.add(new Empty());

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
        sub1.cancel();
        sub2.cancel();
    }

}
