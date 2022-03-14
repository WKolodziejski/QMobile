package com.tinf.qmobile.service;

import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.fragment.SettingsFragment.ALERT;
import static com.tinf.qmobile.fragment.SettingsFragment.CHECK;
import static com.tinf.qmobile.fragment.SettingsFragment.MOBILE;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Works {
    private final static String TAG = "Scheduler";
    private final static WorkManager workManager = WorkManager.getInstance(getContext());

    public static void scheduleParser() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (!prefs.getBoolean(CHECK, true))
            return;

        Constraints.Builder constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED);

        if (prefs.getBoolean(MOBILE, false)) {
            constraints.setRequiredNetworkType(NetworkType.CONNECTED);
            Log.i(TAG, "Mobile data on");
        }

        long time = prefs.getBoolean(ALERT, false) ? 1 : 5;

        PeriodicWorkRequest.Builder work = new PeriodicWorkRequest.Builder(ParserWorker.class,
                time, HOURS,
                15, MINUTES)
                .setConstraints(constraints.build());

        work.setInitialDelay(time, HOURS);

        workManager.cancelUniqueWork("background");
        workManager.enqueueUniquePeriodicWork("background",
                ExistingPeriodicWorkPolicy.REPLACE, work.build());

        Log.i(TAG, "Parser scheduled");
    }

    public static void scheduleAlarm(Data input, long time, boolean cancel) {
        String name = String.valueOf(input.getInt("ID", 0));

        if (cancel) {
            workManager.cancelUniqueWork(name);
            return;
        }

        OneTimeWorkRequest.Builder work = new OneTimeWorkRequest.Builder(AlarmWorker.class)
                .setInputData(input);

        Log.i(TAG, "Alarm scheduled for " + new Date(time));

        time -= Calendar.getInstance().getTimeInMillis();

        if (time < 0)
            return;

        Log.i(TAG, "Delay of " + time + "ms");

        work.setInitialDelay(time, TimeUnit.MILLISECONDS);

        workManager.enqueueUniqueWork(name, ExistingWorkPolicy.REPLACE, work.build());
    }

    public static void cancelAll() {
        workManager.cancelAllWork();
    }

}
