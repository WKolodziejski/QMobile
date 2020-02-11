package com.tinf.qmobile.data;

import android.util.Log;

import com.tinf.qmobile.App;
import com.tinf.qmobile.model.MyObjectBox;
import com.tinf.qmobile.model.calendar.base.EventBase;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.utility.User;
import java.util.List;
import io.objectbox.BoxStore;
import static com.tinf.qmobile.network.Client.pos;

public class DataBase {
    private static final String TAG = "DataBase";
    private static DataBase instance;
    private BoxStore boxStore;
    private List<Matter> matters;
    private List<Schedule> schedule;
    private List<EventBase> events;

    private DataBase() {
        boxStore = MyObjectBox
                .builder()
                .androidContext(App.getContext())
                .build();

        boxStore.subscribe(Matter.class)
                .observer(data -> {
                    if (matters == null) {
                        matters = boxStore
                                .boxFor(Matter.class)
                                .query()
                                .order(Matter_.title_)
                                .equal(Matter_.year_, User.getYear(pos))
                                .and()
                                .equal(Matter_.period_, User.getPeriod(pos))
                                .build()
                                .find();

                        Log.d(TAG, "First fetch");

                    } else {
                        List<Matter> updated = boxStore
                                .boxFor(Matter.class)
                                .query()
                                .order(Matter_.title_)
                                .equal(Matter_.year_, User.getYear(pos))
                                .and()
                                .equal(Matter_.period_, User.getPeriod(pos))
                                .build()
                                .find();

                        for (Matter m1 : updated)
                            for (Matter m2 : matters)
                                if (m1.id == m2.id) {
                                    m1.isExpanded = m2.isExpanded;
                                    m1.shouldAnimate = m2.shouldAnimate;
                                }

                        matters.clear();
                        matters = updated;

                        Log.d(TAG, "List updated");
                    }
                });
    }

    public static synchronized DataBase get() {
        if (instance == null)
            instance = new DataBase();
        return instance;
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }

    public void closeBoxStore() {
        if (boxStore != null) {
            boxStore.closeThreadResources();
            boxStore.close();
            boxStore.deleteAllFiles();
            boxStore = null;
        }
    }


    public List<Matter> getMatters() {
        return matters;
    }

    public List<Schedule> getSchedule() {
        return schedule;
    }

    public List<EventBase> getEvents() {
        return events;
    }

}
