package com.tinf.qmobile.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MateriaActivity;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.tinf.qmobile.activity.settings.SettingsActivity.ALERT;
import static com.tinf.qmobile.activity.settings.SettingsActivity.CHECK;
import static com.tinf.qmobile.activity.settings.SettingsActivity.MOBILE;
import static java.util.concurrent.TimeUnit.HOURS;

public class Jobs {
    private final static String TAG = "JobScheduler";

    public static void scheduleJob(boolean retryError) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(App.getContext()));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext());

        if (prefs.getBoolean(CHECK, true)) {
            Job.Builder diarios = dispatcher.newJobBuilder()
                    .setService(BackgroundCheck.class)
                    .setTag("Background")
                    .setRecurring(false)
                    .setLifetime(Lifetime.FOREVER)
                    .setReplaceCurrent(true)
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .setConstraints(Constraint.ON_UNMETERED_NETWORK);

            if (prefs.getBoolean(MOBILE, false)) {
                diarios.addConstraint(Constraint.ON_ANY_NETWORK);
                Log.i(TAG, "Mobile data on");
            }

            if (retryError) {
                diarios.setTrigger(Trigger.executionWindow((int) HOURS.toSeconds(1), (int) HOURS.toSeconds(2)));
                Log.i(TAG, "Retry");
            } else {
                if (prefs.getBoolean(ALERT, false)) {
                    diarios.setTrigger(Trigger.executionWindow((int) HOURS.toSeconds(3), (int) HOURS.toSeconds(5)));
                    Log.i(TAG, "Alert mode");
                } else {
                    diarios.setTrigger(Trigger.executionWindow((int) HOURS.toSeconds(20), (int) HOURS.toSeconds(24)));
                    Log.i(TAG, "Normal mode");
                }
            }
            dispatcher.cancel("Background");
            dispatcher.schedule(diarios.build());
            Log.i(TAG, "Job scheduled");

        } else {
            dispatcher.cancel("Background");
            Log.i(TAG, "All jobs cancelled");
        }
    }

    public static void cancelAllJobs() {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(App.getContext()));
        dispatcher.cancelAll();
    }

    public static void displayNotification(Context context, String title, String txt, String channelID, int id, Intent intent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelID);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(txt);
        bigText.setBigContentTitle(title);
        bigText.setSummaryText(channelID);

        mBuilder.setSmallIcon(R.drawable.ic_launcher_white);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_white));
        mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(txt);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotificationManager != null) {
            if (mNotificationManager.getNotificationChannel(channelID) == null) {
                mNotificationManager.createNotificationChannel(new NotificationChannel(channelID, channelID,
                        NotificationManager.IMPORTANCE_DEFAULT));
            }
        }

        mBuilder.setChannelId(channelID);
        mBuilder.setAutoCancel(true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MateriaActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        if (mNotificationManager != null) {
            mNotificationManager.notify(id, mBuilder.build());
        }
    }

}
