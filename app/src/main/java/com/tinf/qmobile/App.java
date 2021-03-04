package com.tinf.qmobile;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.service.Jobs;
import com.tinf.qmobile.utility.User;
import io.objectbox.BoxStore;
import static com.tinf.qmobile.fragment.SettingsFragment.DATA;
import static com.tinf.qmobile.fragment.SettingsFragment.NIGHT;
import static com.tinf.qmobile.utility.User.PASSWORD;
import static com.tinf.qmobile.utility.User.REGISTRATION;

public class App extends Application {
    public static final String VERSION = ".v1.3.7";
    public static final String VERSION_INFO = ".Version";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG) {

            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            if (prefs.getBoolean(DATA, false)) {
                crashlytics.setCustomKey("Register", User.getCredential(REGISTRATION));
                crashlytics.setCustomKey("Password", User.getCredential(PASSWORD));
                crashlytics.setCustomKey("URL", User.getURL());
            }
        }

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        AppCompatDelegate.setDefaultNightMode(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(NIGHT, false) ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        context = getApplicationContext();

        if (getSharedPreferences(VERSION_INFO, MODE_PRIVATE).getBoolean(VERSION, true)) {
            if (BoxStore.deleteAllFiles(getApplicationContext(), User.getCredential(REGISTRATION))) {
                Client.get().close();
                Jobs.cancelAllJobs();
                DataBase.get().close();
                User.clearInfos();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().clear().apply();
                getSharedPreferences(VERSION_INFO, MODE_PRIVATE).edit().putBoolean(VERSION, false).apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    public static Context getContext() {
        return context;
    }

}
