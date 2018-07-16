package com.qacademico.qacademico.Custom.Widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.alamkanak.weekview.WeekView;

public class CustomWeekView extends WeekView {
    public CustomWeekView(Context context) {
        super(context);
    }

    public CustomWeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomWeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
