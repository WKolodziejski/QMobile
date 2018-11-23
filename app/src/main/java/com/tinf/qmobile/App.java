package com.tinf.qmobile;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.tinf.qmobile.Class.MyObjectBox;
import io.fabric.sdk.android.Fabric;
import io.objectbox.BoxStore;
import static com.tinf.qmobile.Utilities.Utils.LOGIN_INFO;
import static com.tinf.qmobile.Utilities.Utils.LOGIN_REGISTRATION;
import static com.tinf.qmobile.Utilities.Utils.LOGIN_VALID;

public class App extends Application {
    private BoxStore boxStore;
    private boolean isLogged;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        App.context = getApplicationContext();
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
        if (getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).getBoolean(LOGIN_VALID, false) || isLogged) {
            boxStore = MyObjectBox
                    .builder()
                    .androidContext(App.this)
                    .name(getSharedPreferences(LOGIN_INFO, MODE_PRIVATE)
                            .getString(LOGIN_REGISTRATION, ""))
                    .build();
        }
    }

    public void logOut() {
        isLogged = false;
        boxStore.close();
        boxStore = null;
    }

    public static Context getAppContext() {
        return App.context;
    }
}
