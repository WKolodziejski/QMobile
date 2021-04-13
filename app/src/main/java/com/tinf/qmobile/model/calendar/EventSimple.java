package com.tinf.qmobile.model.calendar;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;

import io.objectbox.annotation.Entity;

import static com.tinf.qmobile.model.ViewType.SIMPLE;
import static com.tinf.qmobile.model.calendar.EventSimple.Type.FIM;
import static com.tinf.qmobile.model.calendar.EventSimple.Type.INICIO;

@Entity
public class EventSimple extends EventBase {
    public enum Type {
        INICIO(1), FIM(2);

        private final int i;

        Type(final int i) {
            this.i = i;
        }

        public int get() {
            return i;
        }
    }

    private int type;

    public EventSimple(String title, long startTime) {
        super(title, startTime);
    }

    public EventSimple(String title, long startTime, long endTime) {
        super(title, startTime, endTime);
    }

    public EventSimple(int type, long startTime) {
        super(type == INICIO.get() ? App.getContext().getString(R.string.calendar_period_start) : App.getContext().getString(R.string.calendar_period_end), startTime);
        this.type = type;
    }

    @Override
    public String getTitle() {
        if (type == INICIO.get()) {
            return App.getContext().getString(R.string.calendar_period_start);
        } else if (type == FIM.get()) {
            return App.getContext().getString(R.string.calendar_period_end);
        } else return super.getTitle();
    }

    /*
     * Required methods
     */

    public EventSimple() {
        super();
    }

    public int getType() {
        return type;
    }

    @Override
    public int getItemType() {
        return SIMPLE;
    }

    @Override
    public boolean equals(CalendarBase event) {
        if (event instanceof EventSimple) {
            EventSimple e = (EventSimple) event;

            return super.equals(event)
                    && e.type == type;
        }
        return false;
    }
}
