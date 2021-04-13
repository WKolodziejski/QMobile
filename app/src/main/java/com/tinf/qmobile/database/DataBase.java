package com.tinf.qmobile.database;

import android.preference.PreferenceManager;
import android.util.Log;

import com.tinf.qmobile.App;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.model.MyObjectBox;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.material.Material;
import com.tinf.qmobile.model.matter.Clazz;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.model.message.Message;
import com.tinf.qmobile.model.message.Message_;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataSubscription;

import static android.content.Context.MODE_PRIVATE;
import static com.tinf.qmobile.App.DATABASE_INFO;
import static com.tinf.qmobile.App.DB_CLASS;
import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.network.Client.pos;

public class DataBase implements OnUpdate {
    private static final String TAG = "DataBase";
    private static DataBase instance;
    private BoxStore boxStore;
    private List<OnDataChange> listeners;
    private final DataSubscription sub1;
    private final DataSubscription sub2;
    private final DataSubscription sub3;
    private final DataSubscription sub4;

    private DataBase() {
        Client.get().addOnUpdateListener(this);

        Log.d("Box for ", User.getCredential(User.REGISTRATION));
        //assert (!User.getCredential(User.REGISTRATION).isEmpty());

        boxStore = MyObjectBox
                .builder()
                .androidContext(getContext())
                .name(User.getCredential(User.REGISTRATION))
                .build();

        sub1 = boxStore.subscribe(Matter.class)
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> update());

        sub2 = boxStore.subscribe(Journal.class)
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> update());

        sub3 = boxStore.subscribe(Material.class)
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> update());

        sub4 = boxStore.subscribe(Message.class)
                .on(AndroidScheduler.mainThread())
                .onError(th -> Log.e(th.getMessage(), th.toString()))
                .observer(data -> countMessages((int) (boxStore.boxFor(Message.class)
                        .query()
                        .equal(Message_.seen_, false)
                        .build()
                        .count())));

        if (getContext().getSharedPreferences(DATABASE_INFO, MODE_PRIVATE).getBoolean(DB_CLASS, true)) {
            getContext().getSharedPreferences(DATABASE_INFO, MODE_PRIVATE).edit().putBoolean(DB_CLASS, false).apply();
            boxStore.boxFor(Clazz.class).removeAll();
        }
    }

    public static synchronized DataBase get() {
        if (instance == null)
            instance = new DataBase();
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
        }
    }

    private void countNotification(int i1, int i2) {
        if (listeners != null)
            for (OnDataChange listener : listeners)
                listener.countNotifications(i1, i2);
    }

    private void countMessages(int i) {
        if (listeners != null)
            for (OnDataChange listener : listeners)
                listener.countMessages(i);
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

    @Override
    public void onScrollRequest() {

    }

    @Override
    public void onDateChanged() {
        update();
    }

}
