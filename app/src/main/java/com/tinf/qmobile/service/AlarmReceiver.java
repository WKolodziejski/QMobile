package com.tinf.qmobile.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.calendar.EventUser_;
import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.matter.Schedule_;

import java.util.Date;
import java.util.List;

import io.objectbox.Box;

import static android.content.Context.ALARM_SERVICE;
import static com.tinf.qmobile.activity.EventCreateActivity.SCHEDULE;
import static com.tinf.qmobile.model.ViewType.USER;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Broadcast received");

        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                if (DataBase.get().getBoxStore() != null) {

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

                    if (alarmManager != null) {

                        Box<EventUser> eventBox = DataBase.get().getBoxStore().boxFor(EventUser.class);
                        List<EventUser> events = eventBox.query().greater(EventUser_.alarm, new Date().getTime() - 1).build().find();

                        for (EventUser event : events) {
                            Intent i = new Intent(context, AlarmReceiver.class);
                            i.putExtra("ID", event.id);
                            i.putExtra("TYPE", USER);

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) event.id, i, 0);

                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, event.getAlarm(), pendingIntent);
                        }

                        Box<Schedule> scheduleBox = DataBase.get().getBoxStore().boxFor(Schedule.class);
                        List<Schedule> schedules = scheduleBox.query().greater(Schedule_.alarm_, new Date().getTime() - 1).build().find();

                        for (Schedule schedule : schedules) {
                            Intent i = new Intent(context, AlarmReceiver.class);
                            i.putExtra("ID", schedule.id);
                            i.putExtra("TYPE", SCHEDULE);

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) schedule.id, i, 0);

                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, schedule.getAlarm(), 24 * 7 * 60 * 60 * 1000, pendingIntent);
                        }
                    }
                    return;
                }
            }
        }

        if (intent.getExtras() != null) {
            long id = intent.getExtras().getLong("ID", 0);

            if (id != 0) {
                if (context != null) {
                    Log.i(TAG, "Calling alarm service");

                    AlarmService.enqueueWork(context, AlarmService.class, (int) id, intent);

                    /*FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

                    Job.Builder alarm = dispatcher.newJobBuilder()
                            .setService(AlarmJob.class)
                            .setTag(String.valueOf(id))
                            .setRecurring(false)
                            .setLifetime(Lifetime.FOREVER)
                            .setReplaceCurrent(true);

                    dispatcher.schedule(alarm.build());*/

                    setResultCode(Activity.RESULT_OK);
                }
            }
        }
    }

}
