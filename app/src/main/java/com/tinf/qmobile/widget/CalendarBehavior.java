package com.tinf.qmobile.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.google.android.material.appbar.AppBarLayout;

/**
 * Attach this behavior to AppBarLayout to disable the bottom portion of a closed appBar
 * so it cannot be touched to open the appBar. This behavior is helpful if there is some
 * portion of the appBar that displays when the appBar is closed, but should not open the appBar
 * when the appBar is closed.
 */
public class CalendarBehavior extends AppBarLayout.Behavior {

    @SuppressWarnings("unused")
    public CalendarBehavior() {
        init();
    }

    @SuppressWarnings("unused")
    public CalendarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return true;
            }
        });
    }

}