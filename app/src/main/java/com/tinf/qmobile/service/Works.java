package com.tinf.qmobile.service;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.fragment.SettingsFragment.ALERT;
import static com.tinf.qmobile.fragment.SettingsFragment.CHECK;
import static com.tinf.qmobile.fragment.SettingsFragment.MOBILE;
import static com.tinf.qmobile.model.ViewType.EVENT;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MATERIAL;
import static com.tinf.qmobile.model.ViewType.MESSAGE;
import static com.tinf.qmobile.model.ViewType.SCHEDULE;
import static java.util.concurrent.TimeUnit.HOURS;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.MatterActivity;

public class Works {
    private final static String TAG = "Scheduler";

    public static void schedule() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (!prefs.getBoolean(CHECK, true)) {
            return;
        }

        Constraints.Builder constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED);

        OneTimeWorkRequest.Builder work = new OneTimeWorkRequest.Builder(ParserWorker.class)
                .setConstraints(constraints.build());

        if (prefs.getBoolean(MOBILE, false)) {
            constraints.setRequiredNetworkType(NetworkType.CONNECTED);
            Log.i(TAG, "Mobile data on");
        }

        work.setInitialDelay(prefs.getBoolean(ALERT, false) ? 5 : 24, HOURS);

        WorkManager.getInstance(getContext()).cancelAllWorkByTag("background");
        WorkManager.getInstance(getContext()).enqueueUniqueWork("background",
                ExistingWorkPolicy.REPLACE, work.build());

        Log.i(TAG, "Work scheduled");
    }

    public static void cancelAll() {
        WorkManager.getInstance(getContext()).cancelAllWork();
    }

    public static void displayNotification(String title, String txt, int channelID, int id, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), getChannelName(channelID));

        NotificationCompat.BigTextStyle text = new NotificationCompat.BigTextStyle();
        text.bigText(txt);
        text.setBigContentTitle(title);
        text.setSummaryText(getChannelName(channelID));

        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_launcher));
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setContentTitle(title);
        builder.setContentText(txt);
        builder.setStyle(text);

        NotificationManager manager = (NotificationManager) getContext()
                .getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && manager != null) {
            if (manager.getNotificationChannel(String.valueOf(channelID)) == null) {
                manager.createNotificationChannel(
                        new NotificationChannel(String.valueOf(channelID), getChannelName(channelID),
                                NotificationManager.IMPORTANCE_DEFAULT));
            }
        }

        builder.setChannelId(String.valueOf(channelID));
        builder.setAutoCancel(true);

        TaskStackBuilder stack = TaskStackBuilder.create(getContext());
        stack.addParentStack(MatterActivity.class);
        stack.addNextIntent(intent);

        PendingIntent pendingIntent = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                stack.getPendingIntent(id,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE) :
                stack.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        if (manager != null) {
            manager.notify(id, builder.build());
        }
    }

    private static String getChannelName(int id) {
        switch (id) {
            case -1:
                return "DEBUG";

            case 0:
                return App.getContext().getResources().getString(R.string.app_name);

            case JOURNAL:
                return App.getContext().getResources().getString(R.string.title_diarios);

            case SCHEDULE:
                return App.getContext().getResources().getString(R.string.title_horario);

            case MATERIAL:
                return App.getContext().getResources().getString(R.string.title_materiais);

            case MESSAGE:
                return App.getContext().getResources().getString(R.string.title_messages);

            case EVENT:
                return App.getContext().getResources().getString(R.string.title_calendario);
        }

        return App.getContext().getResources().getString(R.string.app_name);
    }

}
