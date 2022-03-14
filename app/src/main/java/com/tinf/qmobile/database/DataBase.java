package com.tinf.qmobile.database;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.utility.UserUtils.REGISTRATION;

import android.app.ActivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
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
import com.tinf.qmobile.utility.UserUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.objectbox.BoxStore;
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
    private final ExecutorService executors;
    private final Handler handler;
    private JournalsDataProvider journalsDataProvider;
    private MaterialsDataProvider materialsDataProvider;
    private EventsDataProvider eventsDataProvider;
    private CalendarDataProvider calendarDataProvider;

    private DataBase() {
        this.listeners = new LinkedList<>();
        this.executors = Executors.newFixedThreadPool(5);
        this.handler = new Handler(Looper.getMainLooper());
        Client.get().addOnUpdateListener(this);

        Bundle bundle = new Bundle();

        Log.d("Box for ", UserUtils.getCredential(UserUtils.REGISTRATION));

        try {
            boxStore = MyObjectBox
                    .builder()
                    .androidContext(getContext())
                    .name(UserUtils.getCredential(UserUtils.REGISTRATION))
                    .build();
        } catch (DbSchemaException e) {
            e.printStackTrace();

            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.log("SCHEMA FAILED");
            bundle.putString("Result", "Schema failed");

            if (BoxStore.deleteAllFiles(getContext(), UserUtils.getCredential(REGISTRATION))) {
                Log.d(TAG, "DB deleted");

                crashlytics.log("DB DELETED");
                bundle.putString("Result", "DB deleted");

                boxStore = MyObjectBox
                        .builder()
                        .androidContext(getContext())
                        .name(UserUtils.getCredential(UserUtils.REGISTRATION))
                        .build();
            } else {
                crashlytics.log("APP DATA DELETED");
                bundle.putString("Result", "App data deleted");

                ((ActivityManager) getContext().getSystemService(ACTIVITY_SERVICE))
                        .clearApplicationUserData();
            }
        }

        FirebaseAnalytics.getInstance(getContext()).logEvent("DataBase", bundle);

        Log.d(TAG, boxStore == null ? "BoxStore is null" : boxStore.diagnose());

        sub1 = boxStore.subscribe(Matter.class)
                .onError(Throwable::printStackTrace)
                .observer(data -> update());

        sub2 = boxStore.subscribe(Journal.class)
                .onError(Throwable::printStackTrace)
                .observer(data -> update());

        sub3 = boxStore.subscribe(Material.class)
                .onError(Throwable::printStackTrace)
                .observer(data -> update());

        sub4 = boxStore.subscribe(Message.class)
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
                    .name(UserUtils.getCredential(UserUtils.REGISTRATION))
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
            executors.shutdown();
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

            if (calendarDataProvider != null)
                calendarDataProvider.close();
        }

        Log.d(TAG, "closed");
        Log.d(TAG, boxStore == null ? "BoxStore is null" : boxStore.diagnose());
    }

    private void countNotification(int i1, int i2) {
        handler.post(() -> {
            for (OnCount listener : listeners)
                listener.onCountNotifications(i1, i2);
        });
    }

    private void countMessages(int i) {
        handler.post(() -> {
            for (OnCount listener : listeners)
                listener.onCountMessages(i);
        });
    }

    private void update() {
        execute(() -> {
            List<Matter> matters = boxStore
                    .boxFor(Matter.class)
                    .query()
                    .order(Matter_.title_)
                    .equal(Matter_.year_, UserUtils.getYear(pos))
                    .and()
                    .equal(Matter_.period_, UserUtils.getPeriod(pos))
                    .build()
                    .find();

            int c1 = 0;
            int c2 = 0;

            for (Matter matter : matters) {
                c1 += matter.getJournalNotSeenCount();
                c2 += matter.getMaterialNotSeenCount();
            }

            countNotification(c1, c2);
        });
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

    public CalendarDataProvider getCalendarDataProvider() {
        if (calendarDataProvider == null)
            calendarDataProvider = new CalendarDataProvider();

        return calendarDataProvider;
    }

    public void execute(Runnable runnable) {
        executors.submit(runnable);
    }

    @Override
    public void onDateChanged() {
        update();
    }

}
