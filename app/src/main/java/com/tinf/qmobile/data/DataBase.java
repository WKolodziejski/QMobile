package com.tinf.qmobile.data;

import android.util.Log;
import com.tinf.qmobile.App;
import com.tinf.qmobile.model.MyObjectBox;
import com.tinf.qmobile.model.calendar.base.EventBase;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.utility.User;
import java.util.ArrayList;
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
    private List<OnDataChange> listeners;

    private DataBase() {
        boxStore = MyObjectBox
                .builder()
                .androidContext(App.getContext())
                .build();

        boxStore.subscribe(Matter.class)
                .observer(data -> update());

        boxStore.subscribe(Journal.class)
                .observer(data -> update());
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


    private void callOnNotification(int c) {
        if (listeners != null)
            for (OnDataChange listener : listeners)
                listener.onNotification(c);
    }

    public void addOnDataChangeListener(OnDataChange onDataChange) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        if (onDataChange != null && !listeners.contains(onDataChange)) {
            listeners.add(onDataChange);
            Log.i(TAG, "Added listener from " + onDataChange);
        }
    }

    public void removeOnDataChangeListener(OnDataChange onDataChange) {
        if (listeners != null && onDataChange != null) {
            listeners.remove(onDataChange);
            Log.i(TAG, "Removed listener from " + onDataChange);
        }
    }

    private void update() {
        matters = boxStore
                .boxFor(Matter.class)
                .query()
                .order(Matter_.title_)
                .equal(Matter_.year_, User.getYear(pos))
                .and()
                .equal(Matter_.period_, User.getPeriod(pos))
                .build()
                .find();

        int c = 0;

        for (Matter matter : matters)
            c += matter.getNotSeenCount();

        callOnNotification(c);
    }

}
