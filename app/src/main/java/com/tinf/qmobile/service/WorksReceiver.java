package com.tinf.qmobile.service;

import static com.tinf.qmobile.model.ViewType.EVENT;
import static com.tinf.qmobile.model.ViewType.SCHEDULE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.work.Data;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.calendar.EventUser_;
import com.tinf.qmobile.model.matter.Schedule;
import com.tinf.qmobile.model.matter.Schedule_;
import com.tinf.qmobile.utility.NotificationUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.objectbox.Box;

public class WorksReceiver extends BroadcastReceiver {
    private static final String TAG = "WorksReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putString("Intent", intent.toString());

        FirebaseAnalytics.getInstance(context).logEvent("Broadcast", bundle);

        Log.i(TAG, "Broadcast received");

        if (intent.getAction() == null)
            return;

        if (!intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
            return;

        if (DataBase.get().getBoxStore() == null)
            return;

        Box<EventUser> eventBox = DataBase.get().getBoxStore().boxFor(EventUser.class);
        Box<Schedule> scheduleBox = DataBase.get().getBoxStore().boxFor(Schedule.class);

        List<EventUser> events = eventBox
                .query()
                .greaterOrEqual(EventUser_.alarm, new Date().getTime())
                .build()
                .find();

        List<Schedule> schedules = scheduleBox
                .query()
                .greaterOrEqual(Schedule_.alarm_, new Date().getTime())
                .build()
                .find();

        for (EventUser event : events) {
            if (event.getAlarm() < Calendar.getInstance().getTimeInMillis())
                continue;

            Data input = new Data.Builder()
                    .putLong("ID", event.id)
                    .putInt("TYPE", EVENT)
                    .build();

            Works.scheduleAlarm(input, event.getAlarm(), false);
        }

        for (Schedule schedule : schedules) {
            Data input = new Data.Builder()
                    .putLong("ID", schedule.id)
                    .putInt("TYPE", SCHEDULE)
                    .build();

            long alarm = schedule.getAlarm();

            Calendar alarmTime = Calendar.getInstance();
            alarmTime.setTime(new Date(alarm));
            alarmTime.add(Calendar.WEEK_OF_YEAR, 1);

            schedule.setAlarm(alarmTime.getTimeInMillis());

            scheduleBox.put(schedule);

            Works.scheduleAlarm(input, alarmTime.getTimeInMillis(), false);
        }
    }

}
