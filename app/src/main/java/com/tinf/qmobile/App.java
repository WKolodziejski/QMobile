package com.tinf.qmobile;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.UserUtils;

import io.objectbox.BoxStore;
import static com.tinf.qmobile.fragment.SettingsFragment.NIGHT;
import static com.tinf.qmobile.utility.UserUtils.PASSWORD;
import static com.tinf.qmobile.utility.UserUtils.REGISTRATION;

public class App extends Application {
    public static final String VERSION = ".v1.8.8";
    public static final String VERSION_INFO = ".Version";
    public static final String USE_INFO = ".use";
    public static final String USE_COUNT = ".count";
    public static final String USE_RATED = ".rated";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getBaseContext();

        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);
        crashlytics.setCustomKey("Register", UserUtils.getCredential(REGISTRATION));
        crashlytics.setCustomKey("Password", UserUtils.getCredential(PASSWORD));
        crashlytics.setCustomKey("Name", UserUtils.getName());
        crashlytics.setCustomKey("URL", UserUtils.getURL());
        crashlytics.setCustomKey("Background", Client.background);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        AppCompatDelegate.setDefaultNightMode(PreferenceManager.getDefaultSharedPreferences(
                getBaseContext()).getBoolean(NIGHT, false) ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        if (getSharedPreferences(VERSION_INFO, MODE_PRIVATE).getBoolean(VERSION, true)) {
            if (BoxStore.deleteAllFiles(getBaseContext(), UserUtils.getCredential(REGISTRATION))) {
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
