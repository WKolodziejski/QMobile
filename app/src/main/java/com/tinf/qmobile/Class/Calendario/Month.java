package com.tinf.qmobile.Class.Calendario;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

@Entity
public class Month implements CalendarBase {

    @Id public long id;
    private long date;
    public ToMany<Event> events;

    public Month(long date) {
        this.date = date;
    }

    public String getMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return new SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.getTime());
    }

    public String getYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return new SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.getTime());
    }

    @Override
    public int getItemType() {
        return CalendarBase.ViewType.MONTH;
    }

    /*
     * Required methods
     */

    public Month() {}

    public long getDate() {
        return date;
    }

}
