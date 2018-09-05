package com.tinf.qacademico;

import android.app.Application;
import com.tinf.qacademico.Class.Materias.MyObjectBox;
import io.objectbox.BoxStore;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_INFO;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_REGISTRATION;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_VALID;

public class App extends Application {
    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();

        if (getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).getBoolean(LOGIN_VALID, false)) {
            boxStore = MyObjectBox
                    .builder()
                    .androidContext(App.this)
                    .name(getSharedPreferences(LOGIN_INFO, MODE_PRIVATE)
                            .getString(LOGIN_REGISTRATION, ""))
                    .build();
        }
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }
}
