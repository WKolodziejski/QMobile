package com.tinf.qmobile;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.utility.Utils;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.service.Jobs;
import com.tinf.qmobile.utility.User;

import io.fabric.sdk.android.Fabric;
import io.objectbox.BoxStore;

import static com.tinf.qmobile.fragment.SettingsFragment.DATA;
import static com.tinf.qmobile.fragment.SettingsFragment.NIGHT;
import static com.tinf.qmobile.utility.Utils.VERSION_INFO;
import static com.tinf.qmobile.utility.User.PASSWORD;
import static com.tinf.qmobile.utility.User.REGISTRATION;

public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(getApplicationContext(), new Crashlytics());

        Fabric.with(new Fabric.Builder(getApplicationContext())
                .kits(new CrashlyticsCore.Builder().build())
                .debuggable(true)
                .build());

        if (!BuildConfig.DEBUG) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            if (prefs.getBoolean(DATA, false)) {
                Crashlytics.setString("Register", User.getCredential(REGISTRATION));
                Crashlytics.setString("Password", User.getCredential(PASSWORD));
                Crashlytics.setString("URL", User.getURL());
            }
        }

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        AppCompatDelegate.setDefaultNightMode(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(NIGHT, false) ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        context = getApplicationContext();

        if (getSharedPreferences(VERSION_INFO, MODE_PRIVATE).getBoolean(Utils.VERSION, true)) {
            if (BoxStore.deleteAllFiles(getApplicationContext(), User.getCredential(REGISTRATION))) {
                Client.get().close();
                Jobs.cancelAllJobs();
                DataBase.get().finalise();
                User.clearInfos();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().clear().apply();
                getSharedPreferences(VERSION_INFO, MODE_PRIVATE).edit().putBoolean(Utils.VERSION, false).apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    public static Context getContext() {
        return context;
    }

}
