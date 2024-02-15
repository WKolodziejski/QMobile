package com.tinf.qmobile.database;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.network.Client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.objectbox.reactive.DataObserver;

public abstract class BaseDataProvider<T> implements OnUpdate {
  protected final List<OnData<T>> listeners;
  protected final DataObserver observer;
  private final ExecutorService executors;
  protected final Handler handler;
  protected List<T> list;

  protected abstract List<T> buildList();

  protected abstract void open();

  protected BaseDataProvider() {
    this.list = new ArrayList<>();
    this.listeners = new LinkedList<>();
    this.executors = Executors.newFixedThreadPool(2);
    this.observer = data -> updateList();
    this.handler = new Handler(Looper.getMainLooper());

    Client.get().addOnUpdateListener(this);

    open();
    updateList();
  }

  public synchronized void updateList() {
    executors.execute(() -> {
      list = buildList();
      handler.post(this::callOnData);
    });
  }

  public void addOnDataListener(OnData<T> onData) {
    Log.d(getClass().getName(), "Added listener from " + onData);

    if (onData != null && !listeners.contains(onData))
      listeners.add(onData);
  }

  public void removeOnDataListener(OnData<T> onData) {
    Log.d(getClass().getName(), "Removed listener from " + onData);

    if (onData != null)
      listeners.remove(onData);
  }

  private void callOnData() {
    for (OnData<T> onData : listeners)
      onData.onUpdate(list);
  }

  public List<T> getList() {
    return list;
  }

  protected void close() {
    executors.shutdown();
  }

  @Override
  public void onDateChanged() {
    updateList();
  }

}
