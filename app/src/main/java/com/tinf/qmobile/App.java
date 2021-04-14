package com.tinf.qmobile;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.tinf.qmobile.utility.User;
import io.objectbox.BoxStore;
import static com.tinf.qmobile.fragment.SettingsFragment.NIGHT;
import static com.tinf.qmobile.utility.User.PASSWORD;
import static com.tinf.qmobile.utility.User.REGISTRATION;

public class App extends Application {
    public static final String VERSION = ".v1.3.7";
    public static final String VERSION_INFO = ".Version";
    public static final String DATABASE_INFO = ".DB";
    public static final String DB_CLASS = ".class";
    public static final String USE_INFO = ".use";
    public static final String USE_COUNT = ".count";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getBaseContext();

        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCrashlyticsCollectionEnabled(true);
        crashlytics.setCustomKey("Register", User.getCredential(REGISTRATION));
        crashlytics.setCustomKey("Password", User.getCredential(PASSWORD));
        crashlytics.setCustomKey("URL", User.getURL());

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        AppCompatDelegate.setDefaultNightMode(PreferenceManager.getDefaultSharedPreferences(
                getBaseContext()).getBoolean(NIGHT, false) ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        if (getSharedPreferences(VERSION_INFO, MODE_PRIVATE).getBoolean(VERSION, true)) {
            if (BoxStore.deleteAllFiles(getBaseContext(), User.getCredential(REGISTRATION))) {
                //Client.get().close();
                //Jobs.cancelAllJobs();
                //DataBase.get().close();
                //User.clearInfos();
                //PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().clear().apply();
                getSharedPreferences(VERSION_INFO, MODE_PRIVATE).edit().putBoolean(VERSION, false).apply();
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    public static Context getContext() {
        return context;
    }

}
