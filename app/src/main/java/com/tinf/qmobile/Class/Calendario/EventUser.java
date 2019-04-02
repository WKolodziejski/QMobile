package com.tinf.qmobile.Class.Calendario;

import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.Base.EventBase;
import com.tinf.qmobile.Class.Materias.Matter;
import com.tinf.qmobile.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.objectbox.annotation.Entity;
import io.objectbox.relation.ToOne;

@Entity
public class EventUser extends EventBase {
    //@Id public long id;
    public ToOne<Matter> matter;
    private long alarm;

    public EventUser(String title, long startTime, long alarm) {
        super(title, startTime);
        this.alarm = alarm;
    }

    public EventUser(String title, long startTime, long endTime, long alarm) {
        super(title, startTime, endTime);
        this.alarm = alarm;
    }

    @Override
    public int getColor() {
        if (super.getColor() != App.getContext().getResources().getColor(R.color.colorAccent)) {
            return super.getColor();

        } else if (matter.getTargetId() != 0) {
            return matter.getTarget().getColor();

        } else return super.getColor();
    }

    public String getMatter() {
        return matter.getTargetId() != 0 ? matter.getTarget().getTitle() : "";
    }

    public String getAlarmDifference() {
        SimpleDateFormat h = new SimpleDateFormat("HH", Locale.getDefault());
        SimpleDateFormat m = new SimpleDateFormat("mm", Locale.getDefault());

        Date dif = new Date(getStartTime() - alarm);

        String alarm = "";

        int hours = dif.getHours();
        int minutes = dif.getMinutes();

        if (hours > 0) {
            alarm += h.format(dif) + " " + "hours";
        }

        if (minutes > 0) {
            if (hours > 0) {
                alarm += " and ";
            }

            alarm += m.format(dif) + " minutes";
        }

        if (minutes > 0 || hours > 0) {
            alarm += " before";
        }

        return alarm;
    }

    /*
     * Required methods
     */

    public EventUser() {
        super();
    }

    public long getAlarm() {
        return alarm;
    }

    @Override
    public int getItemType() {
        return ViewType.USER;
    }

}
