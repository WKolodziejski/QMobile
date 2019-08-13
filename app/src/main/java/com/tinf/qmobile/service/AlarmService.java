package com.tinf.qmobile.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.activity.settings.SplashActivity;
import com.tinf.qmobile.App;
import com.tinf.qmobile.model.calendario.EventUser;
import com.tinf.qmobile.R;
import java.text.SimpleDateFormat;
import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import io.objectbox.Box;

public class AlarmService extends JobIntentService {
    private static final String TAG = "AlarmService";

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i(TAG, "Starting job");

        if (intent.getExtras() != null) {

            Box<EventUser> eventBox = App.getBox().boxFor(EventUser.class);
            EventUser event = eventBox.get(intent.getExtras().getLong("ID"));

            if (event != null) {

                String title = event.getTitle();

                if (title.isEmpty()) {
                    title = getString(R.string.event_no_title);
                }

                SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.getDefault());

                String desc = time.format(event.getStartTime());

                if (event.getEndTime() != 0) {
                    desc += " ー " + time.format(event.getEndTime());
                }

                Log.i(TAG, "Sending notification");
                displayNotification(title, desc, getString(R.string.title_calendario), (int) event.id, intent.getExtras());
            }
        }
    }

    public void displayNotification(String title, String txt, String channelID, int id, Bundle extras) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(App.getContext(), channelID);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(txt);
        bigText.setBigContentTitle(title);
        bigText.setSummaryText(channelID);

        mBuilder.setSmallIcon(R.drawable.ic_launcher_white);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.ic_launcher_white));
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

        Intent resultIntent = extras == null ? new Intent(getApplicationContext(), SplashActivity.class) : new Intent(getApplicationContext(), EventViewActivity.class).putExtras(extras);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(App.getContext());
        stackBuilder.addParentStack(EventViewActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        if (mNotificationManager != null) {
            mNotificationManager.notify(id, mBuilder.build());
        }
    }
}
