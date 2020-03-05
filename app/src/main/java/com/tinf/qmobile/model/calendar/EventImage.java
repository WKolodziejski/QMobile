package com.tinf.qmobile.model.calendar;

import android.util.Log;

import com.tinf.qmobile.R;
import com.tinf.qmobile.model.calendar.base.CalendarBase;
import com.tinf.qmobile.model.calendar.base.EventBase;

import io.objectbox.annotation.Entity;

@Entity
public class EventImage extends EventBase {
    @ImageType private int img;

    public EventImage(String title, long startTime, @ImageType int img) {
        super(title, startTime);
        this.img = img;
        Log.d("INNER CLASS", String.valueOf(img));
    }

    public EventImage(String title, long startTime, long endTime, @ImageType int img) {
        super(title, startTime, endTime);
        this.img = img;
    }

    public int getImage() {
        Log.d("IMAGE TYPE", String.valueOf(img));

        switch (img) {
            case ImageType.CHRISTMAS: return R.mipmap.img_christmas;

            case ImageType.VACATION: return R.mipmap.img_vacation;

            case ImageType.CARNIVAL: return R.mipmap.img_carnaval;

            case ImageType.RECESS: return R.mipmap.img_recess;
        }

        return 0;
    }

    /*
     * Required methods
     */

    public EventImage() {
        super();
    }

    @ImageType
    public int getImg() {
        return img;
    }

    @Override
    public int getItemType() {
        return ViewType.IMAGE;
    }

    @Override
    public boolean equals(CalendarBase event) {
        if (event instanceof EventImage) {
            EventImage e = (EventImage) event;

            return super.equals(event)
                    && e.img == img;
        }
        return false;
    }

}
