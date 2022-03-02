package com.tinf.qmobile.database;

import android.os.Handler;
import android.os.Looper;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.network.Client;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import io.objectbox.reactive.DataObserver;

public abstract class BaseDataProvider<T> implements OnUpdate {
    protected final List<OnData> listeners;
    protected final DataObserver observer;
    protected final Handler handler;
    protected List<T> list;

    protected abstract List<T> buildList();
    protected abstract void open();
    protected abstract void close();

    public BaseDataProvider() {
        this.list = new ArrayList<>();
        this.listeners = new LinkedList<>();
        this.observer = data -> updateList();
        this.handler = new Handler(Looper.getMainLooper());

        Client.get().addOnUpdateListener(this);

        open();
        updateList();
    }

    private void updateList() {
        Executors.newSingleThreadExecutor().execute(() -> {
            list = buildList();
            handler.post(this::callOnData);
        });
    }

    public void addOnDataListener(OnData onData) {
        if (onData != null && !listeners.contains(onData))
            listeners.add(onData);
    }

    public void removeOnDataListener(OnData onData) {
        if (onData != null)
            listeners.remove(onData);
    }

    private void callOnData() {
        for (OnData onData : listeners)
            onData.onUpdate(list);
    }

    public List<T> getList() {
        return list;
    }

    @Override
    public void onDateChanged() {
        updateList();
    }

}
