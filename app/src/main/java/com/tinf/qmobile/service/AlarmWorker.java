package com.tinf.qmobile.service;

import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.model.ViewType.EVENT;
import static com.tinf.qmobile.model.ViewType.SCHEDULE;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.EventViewActivity;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.model.calendar.EventUser;
import com.tinf.qmobile.model.matter.Schedule;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import io.objectbox.Box;

public class AlarmWorker extends Worker {

    public AlarmWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int type = getInputData().getInt("TYPE", 0);

        String title = "";
        String desc = "";
        long alarm = 0;
        //String channel = "";

        long id = getInputData().getLong("ID", 0);

        if (type == EVENT) {

            //channel = getContext().getString(R.string.title_calendario);

            Box<EventUser> eventBox = DataBase.get().getBoxStore().boxFor(EventUser.class);
            EventUser event = eventBox.get(id);

            if (event != null) {

                title = event.getTitle();

                SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.getDefault());

                desc = time.format(event.getStartTime());

                if (event.getEndTime() != 0) {
                    desc = desc.concat(" ー " + time.format(event.getEndTime()));
                }

                alarm = event.getAlarm();

            }
        } else if (type == SCHEDULE) {

            //channel = getContext().getString(R.string.title_horario);

            Box<Schedule> scheduleBox = DataBase.get().getBoxStore().boxFor(Schedule.class);
            Schedule schedule = scheduleBox.get(id);

            if (schedule != null) {

                title = schedule.getTitle();

                desc = String.format(Locale.getDefault(), "%02d:%02d", schedule.getStartTime().getHour(), schedule.getStartTime().getMinute());

                if (!schedule.getEndTime().equals(schedule.getStartTime())) {
                    desc = desc.concat(" ー " + String.format(Locale.getDefault(), "%02d:%02d", schedule.getEndTime().getHour(), schedule.getEndTime().getMinute()));
                }

                alarm = schedule.getAlarm();
            }
        }

        Log.d("EVENT", String.valueOf(id));

        if (title.isEmpty()) {
            title = getContext().getString(R.string.event_no_title);
        }

        if (alarm < Calendar.getInstance().getTimeInMillis())
            return Result.failure();

        Intent intent = new Intent(getContext(), EventViewActivity.class);
        intent.putExtra("TYPE", type);
        intent.putExtra("ID", id);
        Works.displayNotification(title, desc, type, (int) id, intent);

        return Result.success();
    }

}
