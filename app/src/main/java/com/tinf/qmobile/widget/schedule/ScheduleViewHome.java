package com.tinf.qmobile.widget.schedule;

import static me.jlurena.revolvingweekview.WeekViewUtil.daysBetween;
import static me.jlurena.revolvingweekview.WeekViewUtil.getPassedMinutesInDay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SoundEffectConstants;
import android.view.View;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.tinf.qmobile.R;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import me.jlurena.revolvingweekview.DayTime;
import me.jlurena.revolvingweekview.WeekViewEvent;
import me.jlurena.revolvingweekview.WeekViewUtil;

public class ScheduleViewHome extends View {

    private final LocalDateTime now;
    private final Context mContext;
    private DayOfWeek mHomeDay;
    private DayOfWeek mMinDay;
    private DayOfWeek mMaxDay;
    //private float mHeaderHeight;
    private GestureDetector mGestureDetector;
    //private OverScroller mScroller;
    private final PointF mCurrentOrigin = new PointF(0f, 0f);
    private Direction mCurrentScrollDirection = Direction.NONE;
    private float mWidthPerDay;
    private Paint mDayBackgroundPaint;
    private Paint mHourSeparatorPaint;
    private Paint mTodayBackgroundPaint;
    private Paint mFutureBackgroundPaint;
    private Paint mPastBackgroundPaint;
    private Paint mFutureWeekendBackgroundPaint;
    private Paint mPastWeekendBackgroundPaint;
    private Paint mNowLinePaint;
    private Paint mEventBackgroundPaint;
    //private float mHeaderColumnWidth;
    private List<EventRect> mEventRects;
    private List<WeekViewEvent> mEvents;
    private TextPaint mEventTextPaint;
    private int mFetchedPeriod = -1; // the middle period the calendar has fetched.
    private boolean mRefreshEvents = false;
    private Direction mCurrentFlingDirection = Direction.NONE;
    private ScaleGestureDetector mScaleDetector;
    private DayOfWeek mFirstVisibleDay;
    private DayOfWeek mLastVisibleDay;
    private EventRect mNewEventRect;
    private TextColorPicker textColorPicker;
    // Attributes and their default values.
    private int mHourHeight = 50;
    private int mNewHourHeight = -1;
    private int mMinHourHeight = 0; //no minimum specified (will be dynamic, based on screen)
    private int mEffectiveMinHourHeight = mMinHourHeight; //compensates for the fact that you can't keep zooming out.
    private int mMaxHourHeight = 250;
    private int mColumnGap = 10;
    private DayOfWeek mFirstDayOfWeek = DayOfWeek.SUNDAY;
    private int mTextSize = 12;
    private int mNumberOfVisibleDays = 3;
    private int mDayBackgroundColor = Color.rgb(245, 245, 245);
    private int mPastBackgroundColor = Color.rgb(227, 227, 227);
    private int mFutureBackgroundColor = Color.rgb(245, 245, 245);
    private int mPastWeekendBackgroundColor = 0;
    private int mFutureWeekendBackgroundColor = 0;
    private int mNowLineColor = Color.rgb(102, 102, 102);
    private int mNowLineThickness = 5;
    private int mHourSeparatorColor = Color.rgb(230, 230, 230);
    private int mTodayBackgroundColor = Color.rgb(239, 247, 254);
    private int mHourSeparatorHeight = 2;
    private int mEventTextSize = 12;
    private int mEventTextColor = Color.BLACK;
    private int mEventPadding = 8;
    private int mDefaultEventColor;
    private int mNewEventColor;
    private String mNewEventIdentifier = "-100";
    private Drawable mNewEventIconDrawable;
    private int mNewEventLengthInMinutes = 60;
    private int mNewEventTimeResolutionInMinutes = 15;
    private boolean mShowFirstDayOfWeekFirst = false;
    private boolean mIsFirstDraw = true;
    private boolean mAreDimensionsInvalid = true;
    private int mOverlappingEventGap = 0;
    private int mEventMarginVertical = 0;
    private DayOfWeek mScrollToDay = null;
    private double mScrollToHour = -1;
    private int mEventCornerRadius = 0;
    private boolean mShowDistinctWeekendColor = false;
    private boolean mShowNowLine = true;
    private boolean mShowDistinctPastFutureColor = false;
    private int mAllDayEventHeight = 100;
    private int mTimeColumnResolution = 60;
    private Typeface mTypeface = Typeface.DEFAULT_BOLD;
    private int mMinTime = 0;
    private int mMaxTime = 24;
    private boolean mAutoLimitTime = false;
    private int mMinOverlappingMinutes = 0;
    // Listeners.
    private EventClickListener mEventClickListener;
    //private EventLongPressListener mEventLongPressListener;
    private WeekViewLoader mWeekViewLoader;
    private EmptyViewClickListener mEmptyViewClickListener;
    //private EmptyViewLongPressListener mEmptyViewLongPressListener;
    private DayTimeInterpreter mDayTimeInterpreter;
    private AddEventClickListener mAddEventClickListener;

    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            /*if (mEventLongPressListener != null && mEventRects != null) {
                List<EventRect> reversedEventRects = mEventRects;
                Collections.reverse(reversedEventRects);
                for (EventRect event : reversedEventRects) {
                    if (event.rectF != null && e.getX() > event.rectF.left && e.getX() < event.rectF.right && e.getY
                            () > event.rectF.top && e.getY() < event.rectF.bottom) {
                        mEventLongPressListener.onEventLongPress(event.originalEvent, event.rectF);
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        return;
                    }
                }
            }

            // If the tap was on in an empty space, then trigger the callback.
            if (mEmptyViewLongPressListener != null) {
                DayTime selectedTime = getTimeFromPoint(e.getX(), e.getY());
                if (selectedTime != null) {
                    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    mEmptyViewLongPressListener.onEmptyViewLongPress(selectedTime);
                }
            }*/
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // If the tap was on an event then trigger the callback.
            if (mEventRects != null && mEventClickListener != null) {
                List<EventRect> reversedEventRects = mEventRects;
                Collections.reverse(reversedEventRects);
                for (EventRect eventRect : reversedEventRects) {
                    if (!mNewEventIdentifier.equals(eventRect.event.getIdentifier()) && eventRect.rectF != null && e
                            .getX() > eventRect.rectF.left && e.getX() < eventRect.rectF.right && e.getY() >
                            eventRect.rectF
                                    .top && e.getY() < eventRect.rectF.bottom) {
                        mEventClickListener.onEventClick(eventRect.originalEvent, eventRect.rectF);
                        playSoundEffect(SoundEffectConstants.CLICK);
                        return super.onSingleTapConfirmed(e);
                    }
                }
            }

            float xOffset = getXStartPixel();

            float x = e.getX() - xOffset;
            float y = e.getY() - mCurrentOrigin.y;
            // If the tap was on add new Event space, then trigger the callback
            if (mAddEventClickListener != null && mNewEventRect != null && mNewEventRect.rectF != null &&
                    mNewEventRect.rectF.contains(x, y)) {
                mAddEventClickListener.onAddEventClicked(mNewEventRect.event.getStartTime(), mNewEventRect.event
                        .getEndTime());
                return super.onSingleTapConfirmed(e);
            }

            // If the tap was on an empty space, then trigger the callback.
            if ((mEmptyViewClickListener != null || mAddEventClickListener != null)) {
                DayTime selectedTime = getTimeFromPoint(e.getX(), e.getY());

                if (selectedTime != null) {
                    List<WeekViewEvent> tempEvents = new ArrayList<>(mEvents);
                    if (mNewEventRect != null) {
                        tempEvents.remove(mNewEventRect.event);
                        mNewEventRect = null;
                    }

                    playSoundEffect(SoundEffectConstants.CLICK);

                    if (mEmptyViewClickListener != null) {
                        mEmptyViewClickListener.onEmptyViewClicked(new DayTime(selectedTime));
                    }

                    if (mAddEventClickListener != null) {
                        //round selectedTime to resolution
                        selectedTime.subtractMinutes(mNewEventLengthInMinutes / 2);
                        //Fix selected time if before the minimum hour
                        if (selectedTime.getMinute() < mMinTime) {
                            selectedTime.setTime(mMinTime, 0);
                        }
                        int unroundedMinutes = selectedTime.getMinute();
                        int mod = unroundedMinutes % mNewEventTimeResolutionInMinutes;
                        selectedTime.addMinutes(mod < Math.ceil(mNewEventTimeResolutionInMinutes / 2) ? -mod :
                                (mNewEventTimeResolutionInMinutes - mod));

                        DayTime endTime = new DayTime(selectedTime);

                        //Minus one to ensure it is the same day and not midnight (next day)
                        int maxMinutes = (mMaxTime - selectedTime.getHour()) * 60 - selectedTime.getMinute() - 1;
                        endTime.addMinutes(Math.min(maxMinutes, mNewEventLengthInMinutes));
                        //If clicked at end of the day, fix selected startTime
                        if (maxMinutes < mNewEventLengthInMinutes) {
                            selectedTime.addMinutes(maxMinutes - mNewEventLengthInMinutes);
                        }

                        WeekViewEvent newEvent = new WeekViewEvent(mNewEventIdentifier, "", null, selectedTime,
                                endTime);

                        float top = mHourHeight * getPassedMinutesInDay(selectedTime) / 60 + getEventsTop();
                        float bottom = mHourHeight * getPassedMinutesInDay(endTime) / 60 + getEventsTop();

                        // Calculate left and right.
                        float left = mWidthPerDay * WeekViewUtil.daysBetween(getFirstVisibleDay(), selectedTime
                                .getDay());
                        float right = left + mWidthPerDay;

                        // Add the new event if its bounds are valid
                        if (left < right &&
                                left < getWidth() &&
                                top < getHeight() &&
                                bottom > 0
                        ) {
                            RectF dayRectF = new RectF(left, top, right, bottom - mCurrentOrigin.y);
                            newEvent.setColor(mNewEventColor);
                            mNewEventRect = new EventRect(newEvent, newEvent, dayRectF);
                            tempEvents.add(newEvent);
                            clearEvents();
                            cacheAndSortEvents(tempEvents);
                            computePositionOfEvents(mEventRects);
                            invalidate();
                        }

                    }
                }

            }
            return super.onSingleTapConfirmed(e);
        }
    };

    public ScheduleViewHome(Context context) {
        this(context, null);
    }

    public ScheduleViewHome(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleViewHome(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        AndroidThreeTen.init(context);
        now = LocalDateTime.now();

        // Hold references.
        mContext = context;

        // Get the attribute values (if any).
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeekView, 0, 0);
        try {
            mFirstDayOfWeek = DayOfWeek.of(a.getInteger(R.styleable.WeekView_firstDayOfWeek, mFirstDayOfWeek.getValue
                    ()));
            mHourHeight = a.getDimensionPixelSize(R.styleable.WeekView_hourHeight, mHourHeight);
            mMinHourHeight = a.getDimensionPixelSize(R.styleable.WeekView_minHourHeight, mMinHourHeight);
            mEffectiveMinHourHeight = mMinHourHeight;
            mMaxHourHeight = a.getDimensionPixelSize(R.styleable.WeekView_maxHourHeight, mMaxHourHeight);
            mTextSize = a.getDimensionPixelSize(R.styleable.WeekView_textSize, (int) TypedValue.applyDimension
                    (TypedValue.COMPLEX_UNIT_SP, mTextSize, context.getResources().getDisplayMetrics()));
            mColumnGap = a.getDimensionPixelSize(R.styleable.WeekView_columnGap, mColumnGap);
            mNumberOfVisibleDays = a.getInteger(R.styleable.WeekView_noOfVisibleDays, mNumberOfVisibleDays);
            mShowFirstDayOfWeekFirst = a.getBoolean(R.styleable.WeekView_showFirstDayOfWeekFirst,
                    mShowFirstDayOfWeekFirst);
            mDayBackgroundColor = a.getColor(R.styleable.WeekView_dayBackgroundColor, mDayBackgroundColor);
            mFutureBackgroundColor = a.getColor(R.styleable.WeekView_futureBackgroundColor, mFutureBackgroundColor);
            mPastBackgroundColor = a.getColor(R.styleable.WeekView_pastBackgroundColor, mPastBackgroundColor);
            mFutureWeekendBackgroundColor = a.getColor(R.styleable.WeekView_futureWeekendBackgroundColor,
                    mFutureBackgroundColor); // If not set, use the same color as in the week
            mPastWeekendBackgroundColor = a.getColor(R.styleable.WeekView_pastWeekendBackgroundColor,
                    mPastBackgroundColor);
            mNowLineColor = a.getColor(R.styleable.WeekView_nowLineColor, mNowLineColor);
            mNowLineThickness = a.getDimensionPixelSize(R.styleable.WeekView_nowLineThickness, mNowLineThickness);
            mHourSeparatorColor = a.getColor(R.styleable.WeekView_hourSeparatorColor, mHourSeparatorColor);
            mTodayBackgroundColor = a.getColor(R.styleable.WeekView_todayBackgroundColor, mTodayBackgroundColor);
            mHourSeparatorHeight = a.getDimensionPixelSize(R.styleable.WeekView_hourSeparatorHeight,
                    mHourSeparatorHeight);
            mEventTextSize = a.getDimensionPixelSize(R.styleable.WeekView_eventTextSize, (int) TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_SP, mEventTextSize, context.getResources()
                            .getDisplayMetrics()));
            mEventTextColor = a.getColor(R.styleable.WeekView_eventTextColor, mEventTextColor);
            mNewEventColor = a.getColor(R.styleable.WeekView_newEventColor, mNewEventColor);
            mNewEventIconDrawable = a.getDrawable(R.styleable.WeekView_newEventIconResource);
            mNewEventIdentifier = (a.getString(R.styleable.WeekView_newEventIdentifier) != null) ? a.getString(R
                    .styleable.WeekView_newEventIdentifier) : mNewEventIdentifier;
            mNewEventLengthInMinutes = a.getInt(R.styleable.WeekView_newEventLengthInMinutes, mNewEventLengthInMinutes);
            mNewEventTimeResolutionInMinutes = a.getInt(R.styleable.WeekView_newEventTimeResolutionInMinutes,
                    mNewEventTimeResolutionInMinutes);
            mEventPadding = a.getDimensionPixelSize(R.styleable.WeekView_eventPadding, mEventPadding);
            mOverlappingEventGap = a.getDimensionPixelSize(R.styleable.WeekView_overlappingEventGap,
                    mOverlappingEventGap);
            mEventMarginVertical = a.getDimensionPixelSize(R.styleable.WeekView_eventMarginVertical,
                    mEventMarginVertical);
            mEventCornerRadius = a.getDimensionPixelSize(R.styleable.WeekView_eventCornerRadius, mEventCornerRadius);
            mShowDistinctPastFutureColor = a.getBoolean(R.styleable.WeekView_showDistinctPastFutureColor,
                    mShowDistinctPastFutureColor);
            mShowDistinctWeekendColor = a.getBoolean(R.styleable.WeekView_showDistinctWeekendColor,
                    mShowDistinctWeekendColor);
            mShowNowLine = a.getBoolean(R.styleable.WeekView_showNowLine, mShowNowLine);
            mAllDayEventHeight = a.getDimensionPixelSize(R.styleable.WeekView_allDayEventHeight, mAllDayEventHeight);
            mTimeColumnResolution = a.getInt(R.styleable.WeekView_timeColumnResolution, mTimeColumnResolution);
            mAutoLimitTime = a.getBoolean(R.styleable.WeekView_autoLimitTime, mAutoLimitTime);
            mMinTime = a.getInt(R.styleable.WeekView_minTime, mMinTime);
            mMaxTime = a.getInt(R.styleable.WeekView_maxTime, mMaxTime);
            mMinOverlappingMinutes = a.getInt(R.styleable.WeekView_minOverlappingMinutes, 0);
        } finally {
            a.recycle();
        }

        init();
    }

    private void cacheAndSortEvents(List<? extends WeekViewEvent> events) {
        for (WeekViewEvent event : events) {
            cacheEvent(event);
        }
        sortEventRects(mEventRects);
    }

    private void cacheEvent(WeekViewEvent event) {
        if (event.getStartTime().compareTo(event.getEndTime()) >= 0) {
            return;
        }
        List<WeekViewEvent> splitedEvents = event.splitWeekViewEvents();
        for (WeekViewEvent splitedEvent : splitedEvents) {
            mEventRects.add(new EventRect(splitedEvent, event, null));
        }

        mEvents.add(event);
    }

    private void calculateHeaderHeight() {
        //Make sure the header is the right size (depends on AllDay events)
        boolean containsAllDayEvent = false;
        if (mEventRects != null && mEventRects.size() > 0) {
            for (int dayNumber = 0;
                 dayNumber < getRealNumberOfVisibleDays();
                 dayNumber++) {
                DayOfWeek day = getFirstVisibleDay().plus(dayNumber);
                for (int i = 0; i < mEventRects.size(); i++) {

                    if (mEventRects.get(i).event.getStartTime().getDay() == day && mEventRects.get(i).event.isAllDay
                            ()) {
                        containsAllDayEvent = true;
                        break;
                    }
                }
                if (containsAllDayEvent) {
                    break;
                }
            }
        }
    }

    private void clearEvents() {
        mEventRects.clear();
        mEvents.clear();
    }

    private void computePositionOfEvents(List<EventRect> eventRects) {
        // Make "collision groups" for all events that collide with others.
        List<List<EventRect>> collisionGroups = new ArrayList<>();
        for (EventRect eventRect : eventRects) {
            boolean isPlaced = false;

            outerLoop:
            for (List<EventRect> collisionGroup : collisionGroups) {
                for (EventRect groupEvent : collisionGroup) {
                    if (isEventsCollide(groupEvent.event, eventRect.event) && groupEvent.event.isAllDay() ==
                            eventRect.event.isAllDay()) {
                        collisionGroup.add(eventRect);
                        isPlaced = true;
                        break outerLoop;
                    }
                }
            }

            if (!isPlaced) {
                List<EventRect> newGroup = new ArrayList<>();
                newGroup.add(eventRect);
                collisionGroups.add(newGroup);
            }
        }

        for (List<EventRect> collisionGroup : collisionGroups) {
            expandEventsToMaxWidth(collisionGroup);
        }
    }

    /**
     * Draw all the Allday-events of a particular day.
     *
     * @param day            The day.
     * @param startFromPixel The left position of the day area. The events will never go any left from this value.
     * @param canvas         The canvas to draw upon.
     */
    private void drawAllDayEvents(DayOfWeek day, float startFromPixel, Canvas canvas) {
        if (mEventRects != null && mEventRects.size() > 0) {
            for (int i = 0; i < mEventRects.size(); i++) {
                if (mEventRects.get(i).event.getStartTime().getDay() == day && mEventRects.get(i).event.isAllDay()) {

                    // Calculate top.
                    float top = mEventMarginVertical;

                    // Calculate bottom.
                    float bottom = top + mEventRects.get(i).bottom;

                    // Calculate left and right.
                    float left = startFromPixel + mEventRects.get(i).left * mWidthPerDay;
                    if (left < startFromPixel) {
                        left += mOverlappingEventGap;
                    }
                    float right = left + mEventRects.get(i).width * mWidthPerDay;
                    if (right < startFromPixel + mWidthPerDay) {
                        right -= mOverlappingEventGap;
                    }

                    // Draw the event and the event name on top of it.
                    if (left < right &&
                            left < getWidth() &&
                            top < getHeight() &&
                            bottom > 0
                    ) {
                        mEventRects.get(i).rectF = new RectF(left, top, right, bottom);
                        mEventBackgroundPaint.setColor(mEventRects.get(i).event.getColor() == 0 ? mDefaultEventColor
                                : mEventRects.get(i).event.getColor());
                        mEventBackgroundPaint.setShader(mEventRects.get(i).event.getShader());
                        canvas.drawRoundRect(mEventRects.get(i).rectF, mEventCornerRadius, mEventCornerRadius,
                                mEventBackgroundPaint);
                        drawEventTitle(mEventRects.get(i).event, mEventRects.get(i).rectF, canvas, top, left);
                    } else {
                        mEventRects.get(i).rectF = null;
                    }
                }
            }
        }
    }

    /**
     * Draw the text on top of the rectangle in the empty event.
     */
    private void drawEmptyImage(RectF rect, Canvas canvas, float originalTop, float originalLeft) {
        int size = Math.max(1, (int) Math.floor(Math.min(0.8 * rect.height(), 0.8 * rect.width())));
        if (mNewEventIconDrawable == null) {
            mNewEventIconDrawable = getResources().getDrawable(android.R.drawable.ic_input_add, null);
        }
        Bitmap icon = ((BitmapDrawable) mNewEventIconDrawable).getBitmap();
        icon = Bitmap.createScaledBitmap(icon, size, size, false);
        canvas.drawBitmap(icon, originalLeft + (rect.width() - icon.getWidth()) / 2, originalTop + (rect.height() -
                icon.getHeight()) / 2, new Paint());

    }

    /**
     * Draw the name of the event on top of the event rectangle.
     *
     * @param event        The event of which the title (and location) should be drawn.
     * @param rect         The rectangle on which the text is to be drawn.
     * @param canvas       The canvas to draw upon.
     * @param originalTop  The original top position of the rectangle. The rectangle may have some of its portion
     *                     outside of the visible area.
     * @param originalLeft The original left position of the rectangle. The rectangle may have some of its portion
     *                     outside of the visible area.
     */
    private void drawEventTitle(WeekViewEvent event, RectF rect, Canvas canvas, float originalTop, float originalLeft) {
        if (rect.right - rect.left - mEventPadding * 2 < 0) {
            return;
        }
        if (rect.bottom - rect.top - mEventPadding * 2 < 0) {
            return;
        }

        // Prepare the name of the event.
        SpannableStringBuilder bob = new SpannableStringBuilder();
        if (!TextUtils.isEmpty(event.getName())) {
            bob.append(event.getName());
            bob.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, bob.length(), 0);
        }
        // Prepare the location of the event.
        if (!TextUtils.isEmpty(event.getLocation())) {
            if (bob.length() > 0) {
                bob.append(' ');
            }
            bob.append(event.getLocation());
        }

        int availableHeight = (int) (rect.bottom - originalTop - mEventPadding * 2);
        int availableWidth = (int) (rect.right - originalLeft - mEventPadding * 2);
        // Clip to paint in left column only.
        canvas.save();
        canvas.clipRect(0, 0, 0, getHeight());
        canvas.restore();

        for (int i = 0; i < getNumberOfPeriods(); i++) {
            // If we are showing half hours (eg. 5:30am), space the times out by half the hour height
            // and need to provide 30 minutes on each odd period, otherwise, minutes is always 0.
            float timeSpacing;
            int minutes;
            int hour;
        }

        // Get text color if necessary
        if (textColorPicker != null) {
            mEventTextPaint.setColor(textColorPicker.getTextColor(event));
        }
        // Get text dimensions.
        StaticLayout textLayout = new StaticLayout(bob, mEventTextPaint, availableWidth, Layout.Alignment
                .ALIGN_NORMAL, 1.0f, 0.0f, false);
        if (textLayout.getLineCount() > 0) {
            int lineHeight = textLayout.getHeight() / textLayout.getLineCount();

            if (availableHeight >= lineHeight) {
                // Calculate available number of line counts.
                int availableLineCount = availableHeight / lineHeight;
                do {
                    // Ellipsize text to fit into event rect.
                    if (!mNewEventIdentifier.equals(event.getIdentifier())) {
                        textLayout = new StaticLayout(TextUtils.ellipsize(bob, mEventTextPaint, availableLineCount *
                                availableWidth, TextUtils.TruncateAt.END), mEventTextPaint, (int) (rect.right -
                                originalLeft
                                - mEventPadding * 2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    }

                    // Reduce line count.
                    availableLineCount--;

                    // Repeat until text is short enough.
                } while (textLayout.getHeight() > availableHeight);

                // Draw text.
                canvas.save();
                canvas.translate(originalLeft + mEventPadding, originalTop + mEventPadding);
                textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    /**
     * Draw all the events of a particular day.
     *
     * @param day            The day.
     * @param startFromPixel The left position of the day area. The events will never go any left from this value.
     * @param canvas         The canvas to draw upon.
     */
    private void drawEvents(DayOfWeek day, float startFromPixel, Canvas canvas) {
        if (mEventRects != null && mEventRects.size() > 0) {
            for (int i = 0; i < mEventRects.size(); i++) {
                if (mEventRects.get(i).event.getStartTime().getDay() == day && !mEventRects.get(i).event.isAllDay()) {
                    float top = mHourHeight * mEventRects.get(i).top / 60 + getEventsTop();
                    float bottom = mHourHeight * mEventRects.get(i).bottom / 60 + getEventsTop();

                    // Calculate left and right.
                    float left = startFromPixel + mEventRects.get(i).left * mWidthPerDay;
                    if (left < startFromPixel) {
                        left += mOverlappingEventGap;
                    }
                    float right = left + mEventRects.get(i).width * mWidthPerDay;
                    if (right < startFromPixel + mWidthPerDay) {
                        right -= mOverlappingEventGap;
                    }

                    // Draw the event and the event name on top of it.
                    if (left < right &&
                            left < getWidth() &&
                            top < getHeight()
                    ) {
                        mEventRects.get(i).rectF = new RectF(left, top, right, bottom);
                        mEventBackgroundPaint.setColor(mEventRects.get(i).event.getColor() == 0 ? mDefaultEventColor
                                : mEventRects.get(i).event.getColor());

                        mEventBackgroundPaint.setShader(mEventRects.get(i).event.getShader());

                        canvas.drawRoundRect(mEventRects.get(i).rectF, mEventCornerRadius, mEventCornerRadius,
                                mEventBackgroundPaint);

                        float topToUse = top;
                        if (mEventRects.get(i).event.getStartTime().getTime().getHour() < mMinTime) {
                            topToUse = mHourHeight * getPassedMinutesInDay(mMinTime, 0) / 60 + getEventsTop();
                        }

                        if (!mNewEventIdentifier.equals(mEventRects.get(i).event.getIdentifier())) {
                            drawEventTitle(mEventRects.get(i).event, mEventRects.get(i).rectF, canvas, topToUse, left);
                        } else {
                            drawEmptyImage(mEventRects.get(i).rectF, canvas, topToUse, left);
                        }

                    } else {
                        mEventRects.get(i).rectF = null;
                    }
                }
            }
        }
    }

    private void drawHeaderRowAndEvents(Canvas canvas) {
        // Calculate the available width for each day.
        mWidthPerDay = getWidth() - mColumnGap * (getRealNumberOfVisibleDays() - 1);
        mWidthPerDay = mWidthPerDay / getRealNumberOfVisibleDays();

        calculateHeaderHeight(); //Make sure the header is the right size (depends on AllDay events)

        LocalDateTime today = now;

        if (mAreDimensionsInvalid) {
            mEffectiveMinHourHeight = Math.max(mMinHourHeight, getHeight() / (mMaxTime - mMinTime));

            mAreDimensionsInvalid = false;
            if (mScrollToDay != null) {
                goToDay(mScrollToDay);
            }

            mAreDimensionsInvalid = false;
            if (mScrollToHour >= 0) {
                goToHour(mScrollToHour);
            }

            mScrollToDay = null;
            mScrollToHour = -1;
            mAreDimensionsInvalid = false;
        }
        if (mIsFirstDraw) {
            mIsFirstDraw = false;

            // If the week view is being drawn for the first time, then consider the first day of the week.
            if (getRealNumberOfVisibleDays() >= 7 && mHomeDay != mFirstDayOfWeek && mShowFirstDayOfWeekFirst) {
                int difference = (mHomeDay.getValue() - mFirstDayOfWeek.getValue());
                mCurrentOrigin.x += (mWidthPerDay + mColumnGap) * difference;
            }
            setLimitTime(mMinTime, mMaxTime);
        }

        // Calculate the new height due to the zooming.
        if (mNewHourHeight > 0) {
            if (mNewHourHeight < mEffectiveMinHourHeight) {
                mNewHourHeight = mEffectiveMinHourHeight;
            } else if (mNewHourHeight > mMaxHourHeight) {
                mNewHourHeight = mMaxHourHeight;
            }

            mHourHeight = mNewHourHeight;
            mNewHourHeight = -1;
        }

        // If the new mCurrentOrigin.y is invalid, make it valid.
        if (mCurrentOrigin.y < getHeight() - mHourHeight * (mMaxTime - mMinTime)) {
            mCurrentOrigin.y = getHeight() - mHourHeight * (mMaxTime - mMinTime);
        }

        // Don't put an "else if" because it will trigger a glitch when completely zoomed out and
        // scrolling vertically.
        if (mCurrentOrigin.y > 0) {
            mCurrentOrigin.y = 0;
        }

        int leftDaysWithGaps = getLeftDaysWithGaps();
        // Consider scroll offset.
        float startFromPixel = getXStartPixel();
        float startPixel = startFromPixel;

        // Prepare to iterate for each hour to draw the hour lines.
        int lineCount = (getHeight() / mHourHeight) + 1;

        lineCount = (lineCount) * (getRealNumberOfVisibleDays() + 1);

        float[] hourLines = new float[lineCount * 4];

        // Clear the cache for event rectangles.
        if (mEventRects != null) {
            for (EventRect eventRect : mEventRects) {
                eventRect.rectF = null;
            }
        }

        // Clip to paint events only.
        canvas.save();
        canvas.clipRect(0, 0, getWidth(), getHeight());

        // Iterate through each day.
        mFirstVisibleDay = mHomeDay;
        mFirstVisibleDay.minus(Math.round(mCurrentOrigin.x / (mWidthPerDay + mColumnGap)));

        if (mAutoLimitTime) {
            List<DayOfWeek> days = new ArrayList<>();
            for (int dayNumber = leftDaysWithGaps + 1;
                 dayNumber <= leftDaysWithGaps + getRealNumberOfVisibleDays();
                 dayNumber++) {
                DayOfWeek day = mHomeDay;
                day.plus(dayNumber - 1);
                days.add(day);
            }
            limitEventTime(days);
        }

        for (int dayNumber = leftDaysWithGaps + 1;
             dayNumber <= leftDaysWithGaps + getRealNumberOfVisibleDays() + 1;
             dayNumber++) {

            // Check if the day is today.
            DayOfWeek day = mHomeDay;
            mLastVisibleDay = day;
            day = day.plus(dayNumber - 1);
            mLastVisibleDay.plus(dayNumber - 2);
            boolean isToday = day == today.getDayOfWeek();

            // Don't draw days which are outside requested range
            if (!dayIsValid(day)) {
                continue;
            }

            // Get more events if necessary. We want to store the events 3 months beforehand. Get
            // events only when it is the first iteration of the loop.
            if (mEventRects == null || mRefreshEvents) {
                getMoreEvents(day);
                mRefreshEvents = false;
            }

            // Draw background color for each day.
            float start = startPixel;
            if (mWidthPerDay + startPixel - start > 0) {
                if (mShowDistinctPastFutureColor) {
                    boolean isWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
                    Paint pastPaint = isWeekend && mShowDistinctWeekendColor ? mPastWeekendBackgroundPaint :
                            mPastBackgroundPaint;
                    Paint futurePaint = isWeekend && mShowDistinctWeekendColor ? mFutureWeekendBackgroundPaint :
                            mFutureBackgroundPaint;
                    float startY = mCurrentOrigin.y;

                    if (isToday) {
                        float beforeNow = (now.getHour() - mMinTime + now.getMinute() / 60.0f) * mHourHeight;
                        canvas.drawRect(start, startY, startPixel + mWidthPerDay, startY + beforeNow, pastPaint);
                        canvas.drawRect(start, startY + beforeNow, startPixel + mWidthPerDay, getHeight(), futurePaint);
                    } else if (day.compareTo(today.getDayOfWeek()) < 0) {
                        canvas.drawRect(start, startY, startPixel + mWidthPerDay, getHeight(), pastPaint);
                    } else {
                        canvas.drawRect(start, startY, startPixel + mWidthPerDay, getHeight(), futurePaint);
                    }
                } else {
                    canvas.drawRect(start, 0, startPixel + mWidthPerDay, getHeight(), isToday ?
                            mTodayBackgroundPaint :
                            mDayBackgroundPaint);
                }
            }

            // Prepare the separator lines for hours.
            int i = 0;
            for (int hourNumber = mMinTime; hourNumber < mMaxTime; hourNumber++) {
                float top = +mCurrentOrigin.y + mHourHeight * (hourNumber -
                        mMinTime);
                if (top > -mHourSeparatorHeight && top < getHeight() && startPixel + mWidthPerDay - start > 0) {
                    hourLines[i * 4] = start;
                    hourLines[i * 4 + 1] = top;
                    hourLines[i * 4 + 2] = startPixel + mWidthPerDay;
                    hourLines[i * 4 + 3] = top;
                    i++;
                }
            }

            // Draw the lines for hours.
            //canvas.drawLines(hourLines, mHourSeparatorPaint);

            // Draw the events.
            drawEvents(day, startPixel, canvas);

            // Draw the line at the current time.
            if (mShowNowLine && isToday) {
                float startY = mCurrentOrigin.y;
                float beforeNow = (now.getHour() - mMinTime + now.getMinute() / 60.0f) * mHourHeight;
                float top = startY + beforeNow;
                canvas.drawLine(start, top, startPixel + mWidthPerDay, top, mNowLinePaint);
            }

            // In the next iteration, start from the next day.
            startPixel += mWidthPerDay + mColumnGap;
        }

        canvas.restore(); // Restore previous clip

        // Hide everything in the first cell (top left corner).
        canvas.save();
        canvas.clipRect(0, 0, 0, 0);
        canvas.restore(); // Restore previous clip

        // Clip to paint header row only.
        canvas.save();
        canvas.clipRect(0, 0, getWidth(), 0);

        // Draw the header row texts.
        startPixel = startFromPixel;
        for (int dayNumber = leftDaysWithGaps + 1; dayNumber <= leftDaysWithGaps + getRealNumberOfVisibleDays() + 1;
             dayNumber++) {
            // Check if the day is today.
            DayOfWeek day = mHomeDay.plus(dayNumber - 1);
            boolean isToday = day == today.getDayOfWeek();

            // Don't draw days which are outside requested range
            if (!dayIsValid(day)) {
                continue;
            }

            // Draw the day labels.
            String dayLabel = getDayTimeInterpreter().interpretDay(day.getValue());
            if (dayLabel == null) {
                throw new IllegalStateException("A DayTimeInterpreter must not return null day");
            }
            //canvas.drawText(dayLabel, startPixel + mWidthPerDay / 2, mHeaderTextHeight + mHeaderRowPadding, isToday ?
            //mTodayHeaderTextPaint : mHeaderTextPaint);
            drawAllDayEvents(day, startPixel, canvas);
            startPixel += mWidthPerDay + mColumnGap;
        }

    }

    /**
     * Expands all the events to maximum possible width. The events will try to occupy maximum
     * space available horizontally.
     *
     * @param collisionGroup The group of events which overlap with each other.
     */
    private void expandEventsToMaxWidth(List<EventRect> collisionGroup) {
        // Expand the events to maximum possible width.
        List<List<EventRect>> columns = new ArrayList<>();
        columns.add(new ArrayList<EventRect>());
        for (EventRect eventRect : collisionGroup) {
            boolean isPlaced = false;
            for (List<EventRect> column : columns) {
                if (column.size() == 0) {
                    column.add(eventRect);
                    isPlaced = true;
                } else if (!isEventsCollide(eventRect.event, column.get(column.size() - 1).event)) {
                    column.add(eventRect);
                    isPlaced = true;
                    break;
                }
            }
            if (!isPlaced) {
                List<EventRect> newColumn = new ArrayList<>();
                newColumn.add(eventRect);
                columns.add(newColumn);
            }
        }

        // Calculate left and right position for all the events.
        // Get the maxRowCount by looking in all columns.
        int maxRowCount = 0;
        for (List<EventRect> column : columns) {
            maxRowCount = Math.max(maxRowCount, column.size());
        }
        for (int i = 0; i < maxRowCount; i++) {
            // Set the left and right values of the event.
            float j = 0;
            for (List<EventRect> column : columns) {
                if (column.size() >= i + 1) {
                    EventRect eventRect = column.get(i);
                    eventRect.width = 1f / columns.size();
                    eventRect.left = j / columns.size();
                    if (!eventRect.event.isAllDay()) {
                        eventRect.top = getPassedMinutesInDay(eventRect.event.getStartTime());
                        eventRect.bottom = getPassedMinutesInDay(eventRect.event.getEndTime());
                    } else {
                        eventRect.top = 0;
                        eventRect.bottom = mAllDayEventHeight;
                    }
                    mEventRects.add(eventRect);
                }
                j++;
            }
        }
    }

    private void getMoreEvents(DayOfWeek day) {

        // Get more events if the month is changed.
        if (mEventRects == null) {
            mEventRects = new ArrayList<>();
        }

        if (mEvents == null) {
            mEvents = new ArrayList<>();
        }

        if (mWeekViewLoader == null && !isInEditMode()) {
            throw new IllegalStateException("You must provide a WeekViewLoader");
        }

        // If a refresh was requested then reset some variables.
        if (mRefreshEvents) {
            this.clearEvents();
            mFetchedPeriod = -1;
        }

        if (mWeekViewLoader != null) {
            if (!isInEditMode() && (mFetchedPeriod < 0 || mRefreshEvents)) {
                List<? extends WeekViewEvent> newEvents = mWeekViewLoader.onWeekViewLoad();

                // Clear events.
                this.clearEvents();
                cacheAndSortEvents(newEvents);
                calculateHeaderHeight();
            }
        }

        // Prepare to calculate positions of each events.
        List<EventRect> tempEvents = mEventRects;
        mEventRects = new ArrayList<>();

        // Iterate through each day with events to calculate the position of the events.
        while (tempEvents.size() > 0) {
            ArrayList<EventRect> eventRects = new ArrayList<>(tempEvents.size());

            // Get first event for a day.
            EventRect eventRect1 = tempEvents.remove(0);
            eventRects.add(eventRect1);

            int i = 0;
            while (i < tempEvents.size()) {
                // Collect all other events for same day.
                EventRect eventRect2 = tempEvents.get(i);
                if (eventRect1.event.getStartTime().getDay() == eventRect2.event.getStartTime().getDay()) {
                    tempEvents.remove(i);
                    eventRects.add(eventRect2);
                } else {
                    i++;
                }
            }
            computePositionOfEvents(eventRects);
        }
    }

    /**
     * Get the time and day where the user clicked on.
     *
     * @param x The x position of the touch event.
     * @param y The y position of the touch event.
     * @return The time and day at the clicked position.
     */
    private DayTime getTimeFromPoint(float x, float y) {
        int leftDaysWithGaps = getLeftDaysWithGaps();
        float startPixel = getXStartPixel();
        for (int dayNumber = leftDaysWithGaps + 1;
             dayNumber <= leftDaysWithGaps + getRealNumberOfVisibleDays() + 1;
             dayNumber++) {
            float start = startPixel;
            if (mWidthPerDay + startPixel - start > 0 && x > start && x < startPixel + mWidthPerDay) {
                DayTime day = new DayTime();
                day.setDay(mHomeDay.plus(dayNumber - 1));
                float pixelsFromZero = y - mCurrentOrigin.y;
                int hour = (int) (pixelsFromZero / mHourHeight);
                int minute = (int) (60 * (pixelsFromZero - hour * mHourHeight) / mHourHeight);
                day.setTime(hour + mMinTime, minute);
                return day;
            }
            startPixel += mWidthPerDay + mColumnGap;
        }
        return null;
    }

    private float getXOriginForDay(DayOfWeek day) {
        return -daysBetween(mHomeDay, day) * (mWidthPerDay + mColumnGap);
    }

    private void goToNearestOrigin() {
        double leftDays = mCurrentOrigin.x / (mWidthPerDay + mColumnGap);

        if (mCurrentFlingDirection != Direction.NONE) {
            // snap to nearest day
            leftDays = Math.round(leftDays);
        } else if (mCurrentScrollDirection == Direction.LEFT) {
            // snap to last day
            leftDays = Math.floor(leftDays);
        } else if (mCurrentScrollDirection == Direction.RIGHT) {
            // snap to next day
            leftDays = Math.ceil(leftDays);
        } else {
            // snap to nearest day
            leftDays = Math.round(leftDays);
        }

        int nearestOrigin = (int) (mCurrentOrigin.x - leftDays * (mWidthPerDay + mColumnGap));
        boolean mayScrollHorizontal = mCurrentOrigin.x - nearestOrigin < getXMaxLimit()
                && mCurrentOrigin.x - nearestOrigin > getXMinLimit();

        if (mayScrollHorizontal) {
            //mScroller.startScroll((int) mCurrentOrigin.x, (int) mCurrentOrigin.y, -nearestOrigin, 0);
            postInvalidateOnAnimation();
        }

        if (nearestOrigin != 0 && mayScrollHorizontal) {
            // Stop current animation.
            // mScroller.forceFinished(true);
            // Snap to day.
            //mScroller.startScroll((int) mCurrentOrigin.x, (int) mCurrentOrigin.y, -nearestOrigin, 0, (int) (Math.abs
            //(nearestOrigin) / mWidthPerDay * mScrollDuration));
            postInvalidateOnAnimation();
        }
        // Reset scrolling and fling direction.
        mCurrentScrollDirection = mCurrentFlingDirection = Direction.NONE;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void init() {
        resetHomeDay();

        mGestureDetector = new GestureDetector(mContext, mGestureListener);

        // Prepare day background color paint.
        mDayBackgroundPaint = new Paint();
        mDayBackgroundPaint.setColor(mDayBackgroundColor);
        mFutureBackgroundPaint = new Paint();
        mFutureBackgroundPaint.setColor(mFutureBackgroundColor);
        mPastBackgroundPaint = new Paint();
        mPastBackgroundPaint.setColor(mPastBackgroundColor);
        mFutureWeekendBackgroundPaint = new Paint();
        mFutureWeekendBackgroundPaint.setColor(mFutureWeekendBackgroundColor);
        mPastWeekendBackgroundPaint = new Paint();
        mPastWeekendBackgroundPaint.setColor(mPastWeekendBackgroundColor);

        // Prepare hour separator color paint.
        mHourSeparatorPaint = new Paint();
        mHourSeparatorPaint.setStyle(Paint.Style.STROKE);
        mHourSeparatorPaint.setStrokeWidth(mHourSeparatorHeight);
        mHourSeparatorPaint.setColor(mHourSeparatorColor);

        // Prepare the "now" line color paint
        mNowLinePaint = new Paint();
        mNowLinePaint.setStrokeWidth(mNowLineThickness);
        mNowLinePaint.setColor(mNowLineColor);

        // Prepare today background color paint.
        mTodayBackgroundPaint = new Paint();
        mTodayBackgroundPaint.setColor(mTodayBackgroundColor);

        // Prepare event background color.
        mEventBackgroundPaint = new Paint();
        mEventBackgroundPaint.setColor(Color.rgb(174, 208, 238));
        // Prepare empty event background color.
        Paint mNewEventBackgroundPaint = new Paint();
        mNewEventBackgroundPaint.setColor(Color.rgb(60, 147, 217));

        // Prepare event text size and color.
        mEventTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        mEventTextPaint.setStyle(Paint.Style.FILL);
        mEventTextPaint.setColor(mEventTextColor);
        mEventTextPaint.setTextSize(mEventTextSize);

        // Set default event color.
        mDefaultEventColor = Color.parseColor("#9fc6e7");
        // Set default empty event color.
        mNewEventColor = Color.parseColor("#3c93d9");

        mScaleDetector = new ScaleGestureDetector(mContext, new WeekViewGestureListener());
    }

    private boolean isEventsCollide(WeekViewEvent event1, WeekViewEvent event2) {
        long start1 = event1.getStartTime().toNumericalUnit();
        long end1 = event1.getEndTime().toNumericalUnit();
        long start2 = event2.getStartTime().toNumericalUnit();
        long end2 = event2.getEndTime().toNumericalUnit();

        long minOverlappingMillis = mMinOverlappingMinutes * 60 * 1000;

        return !((start1 + minOverlappingMillis >= end2) || (end1 <= start2 + minOverlappingMillis));
    }

    /**
     * limit current time of event by update mMinTime & mMaxTime
     * find smallest of start time & latest of end time
     */
    private void limitEventTime(List<DayOfWeek> days) {
        if (mEventRects != null && mEventRects.size() > 0) {
            DayTime startTime = null;
            DayTime endTime = null;

            for (EventRect eventRect : mEventRects) {
                for (DayOfWeek day : days) {
                    if (eventRect.event.getStartTime().getDay() == day && !eventRect.event.isAllDay()) {

                        if (startTime == null || getPassedMinutesInDay(startTime) > getPassedMinutesInDay(eventRect
                                .event.getStartTime())) {
                            startTime = eventRect.event.getStartTime();
                        }

                        if (endTime == null || getPassedMinutesInDay(endTime) < getPassedMinutesInDay(eventRect.event
                                .getEndTime())) {
                            endTime = eventRect.event.getEndTime();
                        }
                    }
                }
            }

            if (startTime != null && endTime != null && startTime.isBefore(endTime)) {
                setLimitTime(Math.max(0, startTime.getHour()), Math.min(24, endTime.getHour() + 1));
            }
        }
    }

    private void recalculateHourHeight() {
        int height = getHeight() / (this.mMaxTime - this.mMinTime);
        if (height > mHourHeight) {
            if (height > mMaxHourHeight) {
                mMaxHourHeight = height;
            }
            mNewHourHeight = height;
        }
    }

    private void resetHomeDay() {
        DayOfWeek newHomeDay = now.getDayOfWeek();

        if (mMinDay != null && newHomeDay.compareTo(mMinDay) < 0) {
            newHomeDay = mMinDay;
        }
        if (mMaxDay != null && newHomeDay.compareTo(mMaxDay) > 0) {
            newHomeDay = mMaxDay;
        }

        if (mMaxDay != null) {
            DayOfWeek day = mMaxDay.plus(1 - getRealNumberOfVisibleDays());
            while (day.compareTo(mMinDay) < 0) {
                day = day.plus(1);
            }

            if (newHomeDay.compareTo(day) > 0) {
                newHomeDay = day;
            }
        }

        mHomeDay = newHomeDay;
    }

    /**
     * Sorts the events in ascending order.
     *
     * @param eventRects The events to be sorted.
     */
    private void sortEventRects(List<EventRect> eventRects) {
        Collections.sort(eventRects, new Comparator<EventRect>() {
            @Override
            public int compare(EventRect left, EventRect right) {
                long start1 = left.event.getStartTime().toNumericalUnit();
                long start2 = right.event.getStartTime().toNumericalUnit();
                int comparator = Long.compare(start1, start2);
                if (comparator == 0) {
                    long end1 = left.event.getEndTime().toNumericalUnit();
                    long end2 = right.event.getEndTime().toNumericalUnit();
                    comparator = Long.compare(end1, end2);
                }
                return comparator;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHeaderRowAndEvents(canvas);
    }

    // fix rotation changes
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mAreDimensionsInvalid = true;
    }

    private enum Direction {
        NONE, LEFT, RIGHT, VERTICAL
    }

    /**
     * A class to hold reference to the events and their visual representation. An EventRect is
     * actually the rectangle that is drawn on the calendar for a given event. There may be more
     * than one rectangle for a single event (an event that expands more than one day). In that
     * case two instances of the EventRect will be used for a single event. The given event will be
     * stored in "originalEvent". But the event that corresponds to rectangle the rectangle
     * instance will be stored in "event".
     */
    private class EventRect {
        WeekViewEvent event;
        WeekViewEvent originalEvent;
        RectF rectF;
        float left;
        float width;
        float top;
        float bottom;

        /**
         * Create a new instance of event rect. An EventRect is actually the rectangle that is drawn
         * on the calendar for a given event. There may be more than one rectangle for a single
         * event (an event that expands more than one day). In that case two instances of the
         * EventRect will be used for a single event. The given event will be stored in
         * "originalEvent". But the event that corresponds to rectangle the rectangle instance will
         * be stored in "event".
         *
         * @param event         Represents the event which this instance of rectangle represents.
         * @param originalEvent The original event that was passed by the user.
         * @param rectF         The rectangle.
         */
        EventRect(WeekViewEvent event, WeekViewEvent originalEvent, RectF rectF) {
            this.event = event;
            this.rectF = rectF;
            this.originalEvent = originalEvent;
        }
    }


    /**
     * A simple GestureListener that holds the focused hour while scaling.
     */
    private class WeekViewGestureListener implements ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            //goToNearestOrigin();
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        }

    }

    public DayTimeInterpreter getDayTimeInterpreter() {
        if (mDayTimeInterpreter == null) {
            mDayTimeInterpreter = new DayTimeInterpreter() {
                @Override
                public String interpretDay(int day) {
                    DayOfWeek dayOfWeek = DayOfWeek.of(day);
                    return mNumberOfVisibleDays > 3
                            ? dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            : dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault());
                }

                @Override
                public String interpretTime(int hour, int minutes) {
                    LocalTime time = LocalTime.of(hour, minutes);
                    return time.format(DateFormat.is24HourFormat(getContext()) ? DateTimeFormatter.ofPattern("H") : DateTimeFormatter.ofPattern
                            ("ha"));
                }
            };
        }
        return mDayTimeInterpreter;
    }

    private float getEventsTop() {
        // Calculate top.
        return mCurrentOrigin.y + mEventMarginVertical - getMinHourOffset();

    }

    public DayOfWeek getFirstVisibleDay() {
        return mFirstVisibleDay;
    }


    private int getLeftDaysWithGaps() {
        return (int) -(Math.ceil(mCurrentOrigin.x / (mWidthPerDay + mColumnGap)));
    }

    private int getMinHourOffset() {
        return mHourHeight * mMinTime;
    }

    private int getNumberOfPeriods() {
        return (int) ((mMaxTime - mMinTime) * (60.0 / mTimeColumnResolution));
    }

    public int getNumberOfVisibleDays() {
        return mNumberOfVisibleDays;
    }

    public int getRealNumberOfVisibleDays() {
        if (mMinDay == null || mMaxDay == null) {
            return getNumberOfVisibleDays();
        }

        return Math.min(mNumberOfVisibleDays, daysBetween(mMinDay, mMaxDay) + 1);
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
        invalidate();
    }

    public void setWeekViewLoader(WeekViewLoader loader) {
        this.mWeekViewLoader = loader;
    }

    private float getXMaxLimit() {
        if (mMinDay == null) {
            return Integer.MAX_VALUE;
        } else {
            return getXOriginForDay(mMinDay);
        }
    }

    private float getXMinLimit() {
        if (mMaxDay == null) {
            return Integer.MIN_VALUE;
        } else {
            DayOfWeek day = mMaxDay.plus(1 - getRealNumberOfVisibleDays());
            while (day.compareTo(mMinDay) < 0) {
                day = day.plus(1);
            }

            return getXOriginForDay(day);
        }
    }

    private float getXStartPixel() {
        return mCurrentOrigin.x + (mWidthPerDay + mColumnGap) * getLeftDaysWithGaps();
    }

    private float getYMaxLimit() {
        return 0;
    }

    private float getYMinLimit() {
        return -(mHourHeight * (mMaxTime - mMinTime)
                - getHeight());
    }

    public void setOnEventClickListener(EventClickListener listener) {
        this.mEventClickListener = listener;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        /*if (mScroller.isFinished()) {
            if (mCurrentFlingDirection != Direction.NONE) {
                // Snap to day after fling is finished.
                goToNearestOrigin();
            }
        } else {
            if (mCurrentFlingDirection != Direction.NONE && forceFinishScroll()) {
                goToNearestOrigin();
            } else if (mScroller.computeScrollOffset()) {
                mCurrentOrigin.y = mScroller.getCurrY();
                mCurrentOrigin.x = mScroller.getCurrX();
                postInvalidateOnAnimation();
            }
        }*/
    }


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean dayIsValid(DayOfWeek day) {
        return (mMinDay == null || day.compareTo(mMinDay) >= 0) && (mMaxDay == null || day.compareTo(mMaxDay) <= 0);
    }

    public void goToDay(DayOfWeek day) {
        //mScroller.forceFinished(true);
        mCurrentScrollDirection = mCurrentFlingDirection = Direction.NONE;

        if (mAreDimensionsInvalid) {
            mScrollToDay = day;
            return;
        }

        mRefreshEvents = true;

        mCurrentOrigin.x = -daysBetween(mHomeDay, day) * (mWidthPerDay + mColumnGap);
        invalidate();
    }

    public void goToHour(double hour) {
        if (mAreDimensionsInvalid) {
            mScrollToHour = hour;
            return;
        }

        int verticalOffset = 0;
        if (hour > mMaxTime) {
            verticalOffset = mHourHeight * (mMaxTime - mMinTime);
        } else if (hour > mMinTime) {
            verticalOffset = (int) (mHourHeight * hour);
        }

        if (verticalOffset > mHourHeight * (mMaxTime - mMinTime) - getHeight()) {
            verticalOffset = (mHourHeight * (mMaxTime - mMinTime) - getHeight());
        }

        mCurrentOrigin.y = -verticalOffset;
        invalidate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        mAreDimensionsInvalid = true;
    }

    public void notifyDatasetChanged() {
        mRefreshEvents = true;
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        boolean val = mGestureDetector.onTouchEvent(event);

        // Check after call of mGestureDetector, so mCurrentFlingDirection and mCurrentScrollDirection are set.
        if (event.getAction() == MotionEvent.ACTION_UP && mCurrentFlingDirection == Direction.NONE) {
            if (mCurrentScrollDirection == Direction.RIGHT || mCurrentScrollDirection == Direction.LEFT) {
                //goToNearestOrigin();
            }
            mCurrentScrollDirection = Direction.NONE;
        }

        return val;
    }

    public void setLimitTime(int startHour, int endHour) {
        if (endHour <= startHour) {
            throw new IllegalArgumentException("endHour must larger startHour.");
        } else if (startHour < 0) {
            throw new IllegalArgumentException("startHour must be at least 0.");
        } else if (endHour > 24) {
            throw new IllegalArgumentException("endHour can't be higher than 24.");
        }
        this.mMinTime = startHour;
        this.mMaxTime = endHour;
        recalculateHourHeight();
        invalidate();
    }

    public interface AddEventClickListener {
        /**
         * Triggered when the users clicks to create a new event.
         *
         * @param startTime The startTime of a new event
         * @param endTime   The endTime of a new event
         */
        void onAddEventClicked(DayTime startTime, DayTime endTime);
    }

    /**
     * Created by Raquib on 1/6/2015.
     */
    public interface DayTimeInterpreter {
        String interpretDay(int day);

        String interpretTime(int hour, int minutes);
    }

    public interface DropListener {
        /**
         * Triggered when view dropped
         *
         * @param view: dropped view.
         * @param day:  object set with the day and time of the dropped coordinates on the view.
         */
        void onDrop(View view, DayTime day);
    }

    public interface EmptyViewClickListener {
        /**
         * Triggered when the users clicks on a empty space of the calendar.
         *
         * @param day: {@link DayTime} object set with the day and time of the clicked position on the view.
         */
        void onEmptyViewClicked(DayTime day);

    }

    public interface EmptyViewLongPressListener {
        /**
         * Similar to {@link me.jlurena.revolvingweekview.WeekView.EmptyViewClickListener} but with long press.
         *
         * @param time: {@link DayTime} object set with the day and time of the long pressed position on the view.
         */
        void onEmptyViewLongPress(DayTime time);
    }

    public interface EventClickListener {
        /**
         * Triggered when clicked on one existing event
         *
         * @param event:     event clicked.
         * @param eventRect: view containing the clicked event.
         */
        void onEventClick(WeekViewEvent event, RectF eventRect);
    }

    public interface EventLongPressListener {
        /**
         * Similar to {@link me.jlurena.revolvingweekview.WeekView.EventClickListener} but with a long press.
         *
         * @param event:     event clicked.
         * @param eventRect: view containing the clicked event.
         */
        void onEventLongPress(WeekViewEvent event, RectF eventRect);
    }

    public interface ScrollListener {
        /**
         * Called when the first visible day has changed.
         * <p>
         * (this will also be called during the first draw of the weekview)
         *
         * @param newFirstVisibleDay The new first visible day
         * @param oldFirstVisibleDay The old first visible day (is null on the first call).
         */
        void onFirstVisibleDayChanged(DayOfWeek newFirstVisibleDay, DayOfWeek oldFirstVisibleDay);
    }

    public interface TextColorPicker {
        int getTextColor(WeekViewEvent event);
    }

    public interface ZoomEndListener {
        /**
         * Triggered when the user finishes a zoom action.
         *
         * @param hourHeight The final height of hours when the user finishes zoom.
         */
        void onZoomEnd(int hourHeight);
    }


    public interface WeekViewLoader {

        /**
         * Load the events within the period
         *
         * @return A list with the events of this period
         */
        List<? extends WeekViewEvent> onWeekViewLoad();
    }

}
