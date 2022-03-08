package com.tinf.qmobile.model.calendar;

import static com.tinf.qmobile.model.ViewType.USER;

import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.matter.Matter;

import java.util.Objects;

import io.objectbox.annotation.Entity;
import io.objectbox.relation.ToOne;

@Entity
public class EventUser extends EventBase {
    //@Id public long id;
    public ToOne<Matter> matter;
    private long alarm;
    private int difference;

    /*public EventUser(String title, long startTime, long alarm, int difference) {
        super(title, startTime);
        this.alarm = alarm;
        this.difference = difference;
    }*/

    public EventUser(String title, long startTime, long endTime, long alarm, int difference) {
        super(title, startTime, endTime);
        this.alarm = alarm;
        this.difference = difference;
    }

    @Override
    public int getColor() {
        return !matter.isNull() ? matter.getTarget().getColor() : super.getColor();
    }

    public String getMatter() {
        return matter.isNull() ? "" : matter.getTarget().getTitle();
    }

    /*public String getAlarmDifference() {
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
    }*/

    /*
     * Required methods
     */

    public EventUser() {
        super();
    }

    public int getDifference() {
        return difference;
    }

    public long getAlarm() {
        return alarm;
    }

    @Override
    public int getItemType() {
        return USER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventUser)) return false;
        if (!super.equals(o)) return false;
        EventUser eventUser = (EventUser) o;
        return getAlarm() == eventUser.getAlarm() && getDifference() == eventUser.getDifference()
                && getMatter().equals(eventUser.getMatter());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMatter(), getAlarm(), getDifference());
    }

    /*@Override
    public boolean equals(CalendarBase event) {
        if (!(event instanceof EventUser)) return false;

        EventUser e = (EventUser) event;

        boolean eq = super.equals(event)
                && e.getAlarm() == getAlarm()
                && e.getDifference() == getDifference();

        if (e.matter.getTargetId() == matter.getTargetId())
            if (matter.getTarget() != null)
                eq = eq && matter.getTarget().equals(e.matter.getTarget());

        return eq;
    }*/

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean isSame(Queryable queryable) {
        return queryable.equals(this);
    }

}
