package com.tinf.qmobile.database;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.utility.User.REGISTRATION;

import android.app.ActivityManager;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.model.MyObjectBox;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.model.message.Message_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;

import java.util.LinkedList;
import java.util.List;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.exception.DbSchemaException;
import io.objectbox.reactive.DataSubscription;

public class DataBase implements OnUpdate {
    private static final String TAG = "DataBase";
    private static DataBase instance;
    private BoxStore boxStore;
    private final List<OnCount> listeners;
    private final DataSubscription sub1;
    private final DataSubscription sub2;
    private final DataSubscription sub3;
    private final DataSubscription sub4;
    private JournalsDataProvider journalsDataProvider;
    private MaterialsDataProvider materialsDataProvider;
    private EventsDataProvider eventsDataProvider;

    private DataBase() {
        this.listeners = new LinkedList<>();
        Client.get().addOnUpdateListener(this);

        Log.d("Box for ", User.getCredential(User.REGISTRATION));

        try {
            boxStore = MyObjectBox
                    .builder()
                    .androidContext(getContext())
                    .name(User.getCredential(User.REGISTRATION))
                    .build();
        } catch (DbSchemaException e) {
            e.printStackTrace();

            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.setCustomKey("DB", "SCHEMA FAILED");

            if (BoxStore.deleteAllFiles(getContext(), User.getCredential(REGISTRATION))) {
                Log.d(TAG, "DB deleted");

                crashlytics.setCustomKey("DB", "DB DELETED");

                boxStore = MyObjectBox
                        .builder()
                        .androidContext(getContext())
                        .name(User.getCredential(User.REGISTRATION))
                        .build();
            } else {
                crashlytics.setCustomKey("DB", "APP DATA DELETED");

                ((ActivityManager) getContext().getSystemService(ACTIVITY_SERVICE))
                        .clearApplicationUserData();
            }
        }

        Log.d(TAG, boxStore == null ? "BoxStore is null" : boxStore.diagnose());

        sub1 = boxStore.subscribe(Matter.class)
                .on(AndroidScheduler.mainThread())
                .onError(Throwable::printStackTrace)
                .observer(data -> update());

        sub2 = boxStore.subscribe(Journal.class)
                .on(AndroidScheduler.mainThread())
                .onError(Throwable::printStackTrace)
                .observer(data -> update());

        sub3 = boxStore.subscribe(Material.class)
                .on(AndroidScheduler.mainThread())
                .onError(Throwable::printStackTrace)
                .observer(data -> update());

        sub4 = boxStore.subscribe(Message.class)
                .on(AndroidScheduler.mainThread())
                .onError(Throwable::printStackTrace)
                .observer(data -> countMessages((int) (boxStore.boxFor(Message.class)
                        .query()
                        .equal(Message_.seen_, false)
                        .build()
                        .count())));
    }

    public static synchronized DataBase get() {
        if (instance == null)
            instance = new DataBase();

        if (instance.boxStore.isClosed())
            instance.boxStore = MyObjectBox.builder()
                    .androidContext(getContext())
                    .name(User.getCredential(User.REGISTRATION))
                    .build();

        return instance;
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }

    public void close() {
        Client.get().removeOnUpdateListener(this);

        if (boxStore != null) {
            sub1.cancel();
            sub2.cancel();
            sub3.cancel();
            sub4.cancel();
            boxStore.closeThreadResources();
            boxStore.close();
            boxStore = null;
            instance = null;

            if (journalsDataProvider != null)
                journalsDataProvider.close();

            if (materialsDataProvider != null)
                materialsDataProvider.close();

            if (eventsDataProvider != null)
                eventsDataProvider.close();
        }

        Log.d(TAG, "closed");
        Log.d(TAG, boxStore == null ? "BoxStore is null" : boxStore.diagnose());
    }

    private void countNotification(int i1, int i2) {
        if (listeners != null)
            for (OnCount listener : listeners)
                listener.onCountNotifications(i1, i2);
    }

    private void countMessages(int i) {
        if (listeners != null)
            for (OnCount listener : listeners)
                listener.onCountMessages(i);
    }

    private void update() {
        List<Matter> matters = boxStore
                .boxFor(Matter.class)
                .query()
                .order(Matter_.title_)
                .equal(Matter_.year_, User.getYear(pos))
                .and()
                .equal(Matter_.period_, User.getPeriod(pos))
                .build()
                .find();

        int c1 = 0;
        int c2 = 0;

        for (Matter matter : matters) {
            c1 += matter.getJournalNotSeenCount();
            c2 += matter.getMaterialNotSeenCount();
        }

        countNotification(c1, c2);
    }

    public void addOnDataChangeListener(OnCount onCount) {
        if (onCount != null && !listeners.contains(onCount)) {
            listeners.add(onCount);
            Log.i(TAG, "Added listener from " + onCount);
        }
    }

    public void removeOnDataChangeListener(OnCount onCount) {
        if (onCount != null) {
            listeners.remove(onCount);
            Log.i(TAG, "Removed listener from " + onCount);
        }
    }

    public JournalsDataProvider getJournalsDataProvider() {
        if (journalsDataProvider == null)
            journalsDataProvider = new JournalsDataProvider();

        return journalsDataProvider;
    }

    public MaterialsDataProvider getMaterialsDataProvider() {
        if (materialsDataProvider == null)
            materialsDataProvider = new MaterialsDataProvider();

        return materialsDataProvider;
    }

    public EventsDataProvider getEventsDataProvider() {
        if (eventsDataProvider == null)
            eventsDataProvider = new EventsDataProvider();

        return eventsDataProvider;
    }

    @Override
    public void onDateChanged() {
        update();
    }

}
