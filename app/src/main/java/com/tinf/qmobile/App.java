package com.tinf.qmobile;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.tinf.qmobile.data.DataBase;
import com.tinf.qmobile.model.calendar.Utils;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.service.Jobs;
import com.tinf.qmobile.utility.User;

import io.objectbox.BoxStore;

import static com.tinf.qmobile.activity.settings.SettingsActivity.NIGHT;
import static com.tinf.qmobile.model.calendar.Utils.VERSION_INFO;
import static com.tinf.qmobile.utility.User.REGISTRATION;

public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        AppCompatDelegate.setDefaultNightMode(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(NIGHT, false) ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        context = getApplicationContext();

        if (getSharedPreferences(VERSION_INFO, MODE_PRIVATE).getBoolean(Utils.VERSION, true)) {
            if (BoxStore.deleteAllFiles(getApplicationContext(), User.getCredential(REGISTRATION))) {
                Client.get().clearRequests();
                Jobs.cancelAllJobs();
                DataBase.get().closeBoxStore();
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
