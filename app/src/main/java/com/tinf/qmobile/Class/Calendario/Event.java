package com.tinf.qmobile.Class.Calendario;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.tinf.qmobile.Class.Materias.Matter;
import com.tinf.qmobile.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class Event extends com.github.sundeepk.compactcalendarview.domain.Event implements CalendarBase {

    @Id public long id;
    private String title;
    private String description;
    private long startTime;
    private long endTime;
    private boolean isFromSite;
    private int color;
    public ToOne<Matter> matter;

    public Event(String title, long startTime, boolean isFromSite) {
        super(0, startTime);
        this.title = title;
        this.startTime = startTime;
        this.isFromSite = isFromSite;
    }

    public String getDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    public String getEndDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTime);
        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    public String getMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        return String.valueOf(calendar.get(Calendar.MONTH));
    }

    public int getImage() {
        if (title.equals("Natal")) {
            return R.mipmap.img_christmas;

        } else if (title.equals("Férias")) {
            return R.mipmap.img_vacation;

        } else if (title.equals("Carnaval")) {
            return R.mipmap.img_carnaval;

        } else if (title.equals("Recesso Escolar")) {
            return R.mipmap.img_recess;

        } else return 0;
    }

    @Override
    public long getTimeInMillis() {
        return startTime;
    }

    @Override
    public int getColor() {
        if (color != 0) {
            return color;

        } else if (matter.getTargetId() != 0) {
            return matter.getTarget().getColor();

        } else return 0;
    }

    @Override
    public int getItemType() {
        if (description != null && !description.isEmpty()) {
            return CalendarBase.ViewType.DEFAULT;

        } else if (title.equals("Natal")) {
            return CalendarBase.ViewType.IMAGE;

        } else if (title.equals("Férias")) {
            return  CalendarBase.ViewType.IMAGE;

        } else if (title.equals("Carnaval")) {
            return  CalendarBase.ViewType.IMAGE;

        } else if (title.equals("Recesso Escolar")) {
            return CalendarBase.ViewType.IMAGE;

        } else if (isFromSite) {
            return CalendarBase.ViewType.SIMPLE;

        } else return CalendarBase.ViewType.DEFAULT;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return format.format(new Date(startTime));
    }

    public String getTitle() {
        return title.isEmpty() ? "(no title)" : title;
    }

    /*
     * Required methods
     */

    public Event() {
        super(0, 0);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isFromSite() {
        return isFromSite;
    }

}
