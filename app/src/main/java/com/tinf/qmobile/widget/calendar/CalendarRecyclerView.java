package com.tinf.qmobile.widget.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarRecyclerView extends RecyclerView {
  private AppBarTracking mAppBarTracking;
  private LinearLayoutManager mLayoutManager;

  public interface AppBarTracking {
    boolean isAppBarIdle();

    boolean isAppBarExpanded();

    int getAppBarOffset();

    int getTopSpace();
  }

  public CalendarRecyclerView(
      @NonNull
      Context context) {
    super(context);
  }

  public CalendarRecyclerView(
      @NonNull
      Context context,
      @Nullable
      AttributeSet attrs) {
    super(context, attrs);
  }

  public CalendarRecyclerView(
      @NonNull
      Context context,
      @Nullable
      AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow,
                                         int type) {
    if (type == ViewCompat.TYPE_NON_TOUCH && mAppBarTracking.isAppBarIdle() &&
        isNestedScrollingEnabled()) {
      if (dy > 0) {
        if (mAppBarTracking.isAppBarExpanded()) {
          consumed[1] = dy;
          return true;
        }
      } else {
        View mView = mLayoutManager.findViewByPosition(mAppBarTracking.getAppBarOffset());
        if (mView != null) {
          consumed[1] = dy - mView.getTop() + mAppBarTracking.getTopSpace();
          return true;
        }
      }
    }

    if (dy < 0 && type == ViewCompat.TYPE_TOUCH && mAppBarTracking.isAppBarExpanded()) {
      consumed[1] = dy;
      return true;
    }

    boolean returnValue = super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);

    if (offsetInWindow != null && !isNestedScrollingEnabled() && offsetInWindow[1] != 0)
      offsetInWindow[1] = 0;

    return returnValue;
  }

  @Override
  public void setLayoutManager(
      @Nullable
      LayoutManager layout) {
    super.setLayoutManager(layout);
    mLayoutManager = (LinearLayoutManager) layout;
  }

  public void setAppBarTracking(AppBarTracking appBarTracking) {
    mAppBarTracking = appBarTracking;
  }

}
