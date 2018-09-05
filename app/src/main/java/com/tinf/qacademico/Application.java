package com.tinf.qacademico;

import io.objectbox.BoxStore;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_INFO;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_REGISTRATION;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_VALID;

public class Application extends android.app.Application {
    //private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();

        /*if (getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).getBoolean(LOGIN_VALID, false)) {
            boxStore = MyObjectBox
                    .builder()
                    .androidContext(Application.this)
                    .name(getSharedPreferences(LOGIN_INFO, MODE_PRIVATE)
                            .getString(LOGIN_REGISTRATION, ""))
                    .build();
        }
    }

    public BoxStore getBoxStore() {
        return boxStore;*/
    }
}
