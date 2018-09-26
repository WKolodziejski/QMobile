package com.tinf.qacademico;

import android.app.Application;
import android.util.Log;

import com.tinf.qacademico.Class.Materias.MyObjectBox;
import com.tinf.qacademico.WebView.SingletonWebView;

import io.objectbox.BoxStore;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_INFO;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_REGISTRATION;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_VALID;

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
        Log.i("BoxStore", "Returned " + boxStore);
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
            Log.i("BoxStore", "Initialized");
        }
    }
}
