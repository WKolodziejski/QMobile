package com.tinf.qmobile;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;
import com.tinf.qmobile.Class.MyObjectBox;
import com.tinf.qmobile.Utilities.User;
import com.tinf.qmobile.Utilities.Utils;

import io.fabric.sdk.android.Fabric;
import io.objectbox.BoxStore;

import static com.tinf.qmobile.Utilities.User.INFO;
import static com.tinf.qmobile.Utilities.User.REGISTRATION;
import static com.tinf.qmobile.Utilities.Utils.VERSION_INFO;

public class App extends Application {
    private BoxStore boxStore;
    private boolean isLogged;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Application", "Initialized");
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        Fabric.with(this, new Crashlytics());
        App.context = getApplicationContext();
        if (getSharedPreferences(VERSION_INFO, MODE_PRIVATE).getBoolean(Utils.VERSION, true)) {
            if (BoxStore.deleteAllFiles(getAppContext(), User.getCredential(App.getAppContext(), REGISTRATION))) {
                getSharedPreferences(VERSION_INFO, MODE_PRIVATE).edit().putBoolean(Utils.VERSION, false).apply();
                User.clearInfos(getApplicationContext());
            }
        }
        initBoxStore();
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
        Log.i("BoxStore", String.valueOf(logged));
    }

    public BoxStore getBoxStore() {
        if (!isLogged || boxStore == null) {
            initBoxStore();
        }
        return boxStore;
    }

    private void initBoxStore() {
        if (boxStore != null) {
            boxStore.close();
        }
        if (User.isValid(context) || isLogged) {
            boxStore = MyObjectBox
                    .builder()
                    .androidContext(this)
                    .name(User.getCredential(getAppContext(), REGISTRATION))
                    .build();
        }
    }

    public void logOut() {
        isLogged = false;
        boxStore.closeThreadResources();
        boxStore.close();
        boxStore.deleteAllFiles();
        boxStore = null;
    }

    public static Context getAppContext() {
        return App.context;
    }
}
