package com.tinf.qmobile;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.tinf.qmobile.Class.MyObjectBox;
import com.tinf.qmobile.Utilities.Jobs;
import com.tinf.qmobile.Utilities.User;
import com.tinf.qmobile.Utilities.Utils;

import io.fabric.sdk.android.Fabric;
import io.objectbox.BoxStore;
import static com.tinf.qmobile.Utilities.User.REGISTRATION;
import static com.tinf.qmobile.Utilities.Utils.VERSION_INFO;

public class App extends Application {
    private static BoxStore boxStore;
    private static boolean isLogged;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Application", "Initialized");

        context = getApplicationContext();

        if (getSharedPreferences(VERSION_INFO, MODE_PRIVATE).getBoolean(Utils.VERSION, true)) {
            if (BoxStore.deleteAllFiles(getApplicationContext(), User.getCredential(REGISTRATION))) {
                getSharedPreferences(VERSION_INFO, MODE_PRIVATE).edit().putBoolean(Utils.VERSION, false).apply();
                User.clearInfos();
            }
        } else {
            initBoxStore();
        }
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
        Log.i("BoxStore", String.valueOf(logged));
    }

    public static BoxStore getBox() {
        if (!isLogged || boxStore == null || boxStore.isClosed()) {
            initBoxStore();
        }
        return boxStore;
    }

    private static void initBoxStore() {
        if (boxStore != null) {
            boxStore.close();
        }
        if (User.isValid() || isLogged) {
            boxStore = MyObjectBox
                    .builder()
                    .androidContext(App.getContext())
                    .name(User.getCredential(REGISTRATION))
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

    public static Context getContext() {
        return context;
    }

}
