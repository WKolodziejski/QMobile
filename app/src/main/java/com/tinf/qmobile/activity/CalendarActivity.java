package com.tinf.qmobile.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.appbar.AppBarLayout;
import com.kodmap.library.kmrecyclerviewstickyheader.KmHeaderItemDecoration;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.EventsAdapter;
import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.model.calendar.Month;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.widget.CalendarRecyclerView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tinf.qmobile.network.OnResponse.PG_CALENDAR;

public class CalendarActivity extends AppCompatActivity implements CalendarRecyclerView.AppBarTracking {
    @BindView(R.id.calendar_appbar)     AppBarLayout appbar;
    @BindView(R.id.calendar_recycler)   CalendarRecyclerView recyclerView;
    @BindView(R.id.calendar_arrow)      ImageView arrow;
    @BindView(R.id.calendar_expand)     LinearLayout expand;
    @BindView(R.id.calendar_title)      TextView title;
    @BindView(R.id.calendar_view)       CompactCalendarView calendar;

    private int appBarOffset = 0;
    private int offset = 0;
    private int topSpace = 0;
    private boolean isExpanded = false;
    private boolean isIdle = true;
    private LinearLayoutManager layout;
    private EventsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar2);
        ButterKnife.bind(this);
        setSupportActionBar(findViewById(R.id.calendar_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layout = new LinearLayoutManager(this);
        adapter = new EventsAdapter(this, calendar);

        recyclerView.setAppBarTracking(this);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new KmHeaderItemDecoration(adapter));

        appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (appBarOffset != verticalOffset) {
                appBarOffset = verticalOffset;
                int totalScrollRange = appBarLayout.getTotalScrollRange();
                float progress = (float) (-verticalOffset) / (float) totalScrollRange;
                arrow.setRotation(-progress * 180);
                isExpanded = verticalOffset == 0;
                isIdle = appBarOffset >= 0 || appBarOffset <= 0;

                if (appBarOffset == -appBarLayout.getTotalScrollRange()) {
                    isExpanded = false;
                    setExpandAndCollapseEnabled(false);
                } else {
                    setExpandAndCollapseEnabled(true);
                }

                if (appBarOffset == 0) {
                    if (isExpanded) {
                        recyclerView.stopScroll();
                        isExpanded = true;
                        offset = layout.findFirstVisibleItemPosition();
                        topSpace = layout.findViewByPosition(layout.findFirstVisibleItemPosition()).getTop();
                    }
                }
            }
        });

        expand.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            recyclerView.stopScroll();
            appbar.setExpanded(isExpanded, true);
        });

        SimpleDateFormat pattern = new SimpleDateFormat("MMMM", Locale.getDefault());

        calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date dateClicked) {
                scrollToDate(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                scrollToDate(firstDayOfNewMonth);
                title.setText(pattern.format(firstDayOfNewMonth));
            }

        });

        title.setText(pattern.format(new Date()));
        scrollToDate(new Date());
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setUseThreeLetterAbbreviation(true);
        calendar.shouldDrawIndicatorsBelowSelectedDays(true);
    }

    private void setExpandAndCollapseEnabled(boolean enabled) {
        if (recyclerView.isNestedScrollingEnabled() != enabled)
            recyclerView.setNestedScrollingEnabled(enabled);
    }

    private void scrollToDate(Date key) {
        List<CalendarBase> array = adapter.getEvents();
        int start = 0;
        int end = array.size() - 1;
        int i = -1;

        while (start <= end) {
            i = (start + end) / 2;
            int comp = array.get(i).getDate().compareTo(key);

            if (0 < comp)
                end = i - 1;

            else if (0 > comp)
                start = i + 1;

            else if (0 == comp)
                break;
        }

        if (i >= 0)
            layout.scrollToPosition(i);
    }

    @Override
    public boolean isAppBarExpanded() {
        return appBarOffset == 0;
    }

    @Override
    public boolean isAppBarIdle() {
        return isIdle;
    }

    @Override
    public int getAppBarOffset() {
        return offset;
    }

    @Override
    public int getTopSpace() {
        return topSpace;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_refresh) {
            Client.get().load(PG_CALENDAR);
            return true;
        } else if (item.getItemId() == R.id.action_today) {
            Client.get().requestScroll();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
