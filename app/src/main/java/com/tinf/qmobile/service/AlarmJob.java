package com.tinf.qmobile.service;

import android.content.Intent;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.matter.Schedule;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.objectbox.Box;

import static com.tinf.qmobile.activity.EventCreateActivity.SCHEDULE;
import static com.tinf.qmobile.model.ViewType.USER;

public class AlarmJob extends JobService {
    private static final String TAG = "AlarmJob";

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.i(TAG, "Starting job");

        if (job.getExtras() != null) {

            int type = job.getExtras().getInt("TYPE", 0);

            String title = "";
            String desc = "";
            String channel = "";

            long id = job.getExtras().getLong("ID");

            if (type == USER) {

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

            Intent intent = new Intent(getBaseContext(), EventViewActivity.class);
            intent.putExtra("TYPE", job.getExtras().getInt("TYPE", 0));
            intent.putExtra("ID", job.getExtras().getLong("ID", 0));
            Jobs.displayNotification(getBaseContext(), title, desc, channel, (int) id, intent);

            Log.i(TAG, "Sending notification");

        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

}
