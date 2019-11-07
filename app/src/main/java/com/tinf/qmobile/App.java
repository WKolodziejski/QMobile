package com.tinf.qmobile;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.tinf.qmobile.model.MyObjectBox;
import com.tinf.qmobile.model.calendar.Utils;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;

import io.objectbox.BoxStore;

import static com.tinf.qmobile.activity.settings.SettingsActivity.NIGHT;
import static com.tinf.qmobile.model.calendar.Utils.VERSION_INFO;
import static com.tinf.qmobile.utility.User.REGISTRATION;

public class App extends Application {
    private static final String TAG = "Application";
    private static BoxStore boxStore;
    //private static boolean isLogged;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(NIGHT, false) ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

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
        if (/*!isLogged ||*/ boxStore == null) {
            //Log.v(TAG, "Login state: " + String.valueOf(isLogged));
            Log.v(TAG, "Box state: " + boxStore);
            initBoxStore();
        }
        return boxStore;
    }

    private static void initBoxStore() {
        Log.v(TAG, "Initializing box...");

        if (User.isValid() || Client.get().isValid()/*|| isLogged*/) {
            Log.v(TAG, "Building box...");
            boxStore = MyObjectBox
                    .builder()
                    .androidContext(App.getContext())
                    .name(User.getCredential(REGISTRATION))
                    .build();
            boxStore.setDbExceptionListener(Throwable::printStackTrace);
        } else {
            Log.e(TAG, "Box not initilized");
        }
    }

    public static void closeBoxStore() {
        if (boxStore != null) {
            boxStore.closeThreadResources();
            boxStore.close();
            boxStore.deleteAllFiles();
            boxStore = null;
        }
    }

    public static Context getContext() {
        return context;
    }

}
