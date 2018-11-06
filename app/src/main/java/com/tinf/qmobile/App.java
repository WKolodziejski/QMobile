package com.tinf.qmobile;

import android.app.Application;
import android.util.Log;
import com.tinf.qmobile.Class.MyObjectBox;
import io.objectbox.BoxStore;
import static com.tinf.qmobile.Utilities.Utils.LOGIN_INFO;
import static com.tinf.qmobile.Utilities.Utils.LOGIN_REGISTRATION;
import static com.tinf.qmobile.Utilities.Utils.LOGIN_VALID;

public class App extends Application {
    private BoxStore boxStore;
    private boolean isLogged;

    @Override
    public void onCreate() {
        super.onCreate();
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
}
