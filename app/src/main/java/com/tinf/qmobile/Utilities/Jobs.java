package com.tinf.qmobile.Utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.tinf.qmobile.Activity.MainActivity;
import com.tinf.qmobile.Activity.MateriaActivity;
import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Service.BackgroundCheck;
import androidx.core.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;
import static java.util.concurrent.TimeUnit.HOURS;

public class Jobs {

    public static void scheduleJob(boolean retryError) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(App.getContext()));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext());

        if (prefs.getBoolean("key_check", true)) {
            Job.Builder diarios = dispatcher.newJobBuilder()
                    .setService(BackgroundCheck.class)
                    .setTag("Diarios")
                    .setRecurring(false)
                    .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                    .setReplaceCurrent(true)
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .setConstraints(Constraint.ON_UNMETERED_NETWORK);

            if (prefs.getBoolean("key_mobile_data", false)) {
                diarios.addConstraint(Constraint.ON_ANY_NETWORK);
                Log.i("JobScheduler", "Mobile data on");
            }

            if (retryError) {
                diarios.setTrigger(Trigger.executionWindow((int) HOURS.toSeconds(1), (int) HOURS.toSeconds(2)));
                Log.i("JobScheduler", "Retry");
            } else {
                if (prefs.getBoolean("key_alert_mode", false)) {
                    diarios.setTrigger(Trigger.executionWindow((int) HOURS.toSeconds(3), (int) HOURS.toSeconds(5)));
                    Log.i("JobScheduler", "Alert mode");
                } else {
                    diarios.setTrigger(Trigger.executionWindow((int) HOURS.toSeconds(20), (int) HOURS.toSeconds(24)));
                    Log.i("JobScheduler", "Normal mode");
                }
            }
            dispatcher.cancelAll();
            dispatcher.schedule(diarios.build());
            Log.i("JobScheduler", "Job scheduled");

        } else {
            dispatcher.cancelAll();
            Log.i("JobScheduler", "All jobs cancelled");
        }
    }

    public static void cancellAllJobs() {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(App.getContext()));
        dispatcher.cancelAll();
    }

    public static void displayNotification(String title, String txt, String channelID, int id, Bundle extras) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(App.getContext(), channelID);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(txt);
        bigText.setBigContentTitle(title);
        bigText.setSummaryText(channelID);

        mBuilder.setSmallIcon(R.drawable.icon_launcher_grey);
        mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(txt);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager = (NotificationManager) App.getContext().getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(new NotificationChannel(channelID, channelID,
                    NotificationManager.IMPORTANCE_DEFAULT));
            mBuilder.setChannelId(channelID);
        }

        mBuilder.setAutoCancel(true);

        Intent resultIntent = extras == null ? new Intent(App.getContext(), MainActivity.class) : new Intent(App.getContext(), MateriaActivity.class).putExtras(extras);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(App.getContext());
        stackBuilder.addParentStack(MateriaActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        if (mNotificationManager != null) {
            mNotificationManager.notify(id, mBuilder.build());
        }
    }
}
