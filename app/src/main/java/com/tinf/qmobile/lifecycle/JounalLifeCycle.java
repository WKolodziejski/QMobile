package com.tinf.qmobile.lifecycle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public abstract class JounalLifeCycle implements LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    abstract public void connectListener();

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    abstract public void disconnectListener();

}
