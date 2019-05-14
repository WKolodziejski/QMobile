package com.tinf.qmobile;

import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.tinf.qmobile.model.MyObjectBox;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;
import com.tinf.qmobile.utility.Utils;

import io.objectbox.BoxStore;
import static com.tinf.qmobile.utility.User.REGISTRATION;
import static com.tinf.qmobile.utility.Utils.VERSION_INFO;

public class App extends Application {
    private static final String TAG = "Application";
    private static BoxStore boxStore;
    //private static boolean isLogged;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

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

    public static BoxStore getBox() {
        if (/*!isLogged ||*/ boxStore == null || boxStore.isClosed()) {
            //Log.v(TAG, "Login state: " + String.valueOf(isLogged));
            Log.v(TAG, "Box state: " + boxStore);
            initBoxStore();
        }
        return boxStore;
    }

    private static void initBoxStore() {
        Log.v(TAG, "Initializing box...");
        if (boxStore != null) {
            Log.w(TAG, "Box already open. Closing...");
            boxStore.close();
        }
        if (User.isValid() || Client.get().isValid()/*|| isLogged*/) {
            Log.v(TAG, "Building box...");
            boxStore = MyObjectBox
                    .builder()
                    .androidContext(App.getContext())
                    .name(User.getCredential(REGISTRATION))
                    .build();
        } else {
            Log.e(TAG, "Box not initilized");
        }
    }

    public void logOut() {
        boxStore.closeThreadResources();
        boxStore.close();
        boxStore.deleteAllFiles();
        boxStore = null;
    }

    public static Context getContext() {
        return context;
    }

}
