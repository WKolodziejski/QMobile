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

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.model.matter.Schedule;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.objectbox.Box;

import static com.tinf.qmobile.activity.EventCreateActivity.SCHEDULE;

public class AlarmService extends JobIntentService {
    private static final String TAG = "AlarmService";

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i(TAG, "Starting job");

        if (intent.getExtras() != null) {

            int type = intent.getIntExtra("TYPE", 0);

            String title = "";
            String desc = "";
            String channel = "";

            long id = intent.getExtras().getLong("ID");

            if (type == CalendarBase.ViewType.USER) {

                channel = getString(R.string.title_calendario);

                Box<EventUser> eventBox = DataBase.get().getBoxStore().boxFor(EventUser.class);
                EventUser event = eventBox.get(id);

                if (event != null) {

                    title = event.getTitle();

                    SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.getDefault());

                    desc = time.format(event.getStartTime());

                    if (event.getEndTime() != 0) {
                        desc = desc.concat(" ー " + time.format(event.getEndTime()));
                    }

                }
            } else if (type == SCHEDULE) {

                channel = getString(R.string.title_horario);

                Box<Schedule> scheduleBox = DataBase.get().getBoxStore().boxFor(Schedule.class);
                Schedule schedule = scheduleBox.get(id);

                if (schedule != null) {

                    title = schedule.getTitle();

                    desc = String.format(Locale.getDefault(), "%02d:%02d", schedule.getStartTime().getHour(), schedule.getStartTime().getMinute());

                    if (!schedule.getEndTime().equals(schedule.getStartTime())) {
                        desc = desc.concat(" ー " + String.format(Locale.getDefault(), "%02d:%02d", schedule.getEndTime().getHour(), schedule.getEndTime().getMinute()));
                    }
                }
            }

            if (title.isEmpty()) {
                title = getString(R.string.event_no_title);
            }

            Log.i(TAG, "Sending notification");
            displayNotification(title, desc, channel, (int) id, intent.getExtras());

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

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(App.getContext());
        stackBuilder.addParentStack(EventViewActivity.class);
        stackBuilder.addNextIntent(new Intent().putExtras(extras));

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        if (mNotificationManager != null) {
            mNotificationManager.notify(id, mBuilder.build());
        }
    }

}
