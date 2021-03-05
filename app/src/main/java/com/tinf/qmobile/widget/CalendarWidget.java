package com.tinf.qmobile.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.tinf.qmobile.R;
import com.tinf.qmobile.model.calendar.Day2;
import com.tinf.qmobile.model.calendar.Month2;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CalendarWidget extends LinearLayout {
    @BindView(R.id.calendar_view)   CompactCalendarView calendar;

    private Context context;
    private MonthChangeListner listener;
    private LocalDate date;
    private int curMonth;

    public interface MonthChangeListner {
        void onMonth(Month2 month);
    }

    public CalendarWidget(Context context) {
        super(context);
        ButterKnife.bind(this, LayoutInflater.from(context).inflate(R.layout.calendar_viewpager, this));
    }

    public CalendarWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ButterKnife.bind(this, LayoutInflater.from(context).inflate(R.layout.calendar_viewpager, this));
    }

    public CalendarWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ButterKnife.bind(this, LayoutInflater.from(context).inflate(R.layout.calendar_viewpager, this));
    }

    public void setMonth(LocalDate month) {
        LocalDate minDate = date.toDateTimeAtStartOfDay().dayOfMonth().withMinimumValue().toLocalDate();
        LocalDate curDate = month.dayOfMonth().withMaximumValue();

        int months = Months.monthsBetween(minDate, curDate).getMonths();

       /* if (viewPager.getCurrentItem() != months) {
            viewPager.setCurrentItem(months,false);
            viewPager.getAdapter().notifyDataSetChanged();
        }*/
    }

    public void init(LocalDate minDate, LocalDate maxDate) {
        minDate = minDate.toDateTimeAtStartOfDay().dayOfMonth().withMinimumValue().toLocalDate();
        maxDate = maxDate.dayOfMonth().withMaximumValue();

        List<Month2> months = new ArrayList<>();
        HashMap<LocalDate, String[]> events = new HashMap<>();

        for (int i = 0; i < Months.monthsBetween(minDate, maxDate).getMonths(); i++) {
            Month2 month = new Month2(minDate);
            int year = new LocalDate().getYear();

            List<Day2> days = new ArrayList<>();
            DateTime startDay = minDate.dayOfMonth().withMinimumValue().toDateTimeAtStartOfDay();
            LocalDate week = startDay.dayOfWeek().withMinimumValue().toLocalDate().minusDays(1);

            while (week.compareTo(startDay.dayOfMonth().withMaximumValue().toLocalDate()) < 0) {
                String pattern = week.getYear() == year ? "d MMM" : "d MMM YYYY";
                String[] e;

                if (week.getMonthOfYear() == week.plusDays(6).getMonthOfYear()) {
                    e = new String[] {week.toString("d").toUpperCase() + " - " + week.plusDays(6).toString(pattern).toUpperCase()};
                } else {
                    e = new String[] {week.toString("d MM").toUpperCase() + " - " + week.plusDays(6).toString(pattern).toUpperCase()};
                }

                if (!events.containsKey(week))
                    events.put(week, e);

                week = week.plusWeeks(1);
            }

            for (int j = 1; j < month.getNoDay(); j++) {
                Day2 day = new Day2(startDay);

                if (events.containsKey(startDay.toLocalDate())) {

                    List<String> list = Arrays.asList(events.get(startDay.toLocalDate()));
                    list.addAll(Arrays.asList(events.get(startDay.toLocalDate())));

                    String[] mStringArray = new String[list.size()];
                    String[] s = list.toArray(mStringArray);

                    events.put(startDay.toLocalDate(), s);
                } else {
                    events.put(startDay.toLocalDate(), events.get(startDay.toLocalDate()));
                }

                if (startDay.toLocalDate().equals(new LocalDate())) {
                    day.setToday(true);
                    curMonth = i;
                } else {
                    day.setToday(false);
                }

                days.add(day);

                if (j == 1) {
                    String[] s = {"start"};

                    if (events.containsKey(startDay.toLocalDate())) {
                        List<String> list = Arrays.asList(events.get(startDay.toLocalDate()));
                        list.add(0, "start");
                        String[] mStringArray = new String[list.size()];
                        s = list.toArray(mStringArray);
                    }

                    events.put(startDay.toLocalDate(), s);
                }

                startDay = startDay.plusDays(1);
            }

            month.setDays(days);
            months.add(month);
            minDate = minDate.plusMonths(1);
        }

    }

}
