package com.tinf.qmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kodmap.library.kmrecyclerviewstickyheader.KmHeaderItemDecoration;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.EventsAdapter;
import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.utility.User;
import com.tinf.qmobile.widget.CalendarRecyclerView;
import org.joda.time.LocalDate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.tinf.qmobile.activity.EventCreateActivity.EVENT;
import static com.tinf.qmobile.network.Client.pos;

public class CalendarActivity extends AppCompatActivity implements CalendarRecyclerView.AppBarTracking, OnResponse {
    @BindView(R.id.calendar_appbar)     AppBarLayout appbar;
    @BindView(R.id.calendar_recycler)   CalendarRecyclerView recyclerView;
    @BindView(R.id.calendar_arrow)      ImageView arrow;
    @BindView(R.id.calendar_expand)     LinearLayout expand;
    @BindView(R.id.calendar_title)      TextView title;
    @BindView(R.id.calendar_view)       CompactCalendarView calendar;
    @BindView(R.id.calendar_refresh)    SwipeRefreshLayout refresh;
    @BindView(R.id.calendar_fab)        FloatingActionButton fab;

    private int appBarOffset = 0;
    private int offset = 0;
    private int topSpace = 0;
    private boolean isExpanded = false;
    private boolean isIdle = true;
    private LinearLayoutManager layout;
    private EventsAdapter adapter;

    private static SimpleDateFormat month = new SimpleDateFormat("MMMM", Locale.getDefault());
    private static SimpleDateFormat year = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);
        setSupportActionBar(findViewById(R.id.calendar_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_cancel));

        Client.get().load(PG_CALENDAR);
        //Client.get().loadYear(pos);

        layout = new LinearLayoutManager(this);
        adapter = new EventsAdapter(this, calendar);

        refresh.setEnabled(false);

        recyclerView.setAppBarTracking(this);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new KmHeaderItemDecoration(adapter));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!isExpanded && newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
                    calendar.setCurrentDate(adapter.getEvents().get(layout.findFirstVisibleItemPosition()).getDate());
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                setDateTitle(adapter.getEvents().get(layout.findFirstVisibleItemPosition()).getDate());

                if (dy < 0 && !fab.isShown())
                    fab.show();
                else if(dy > 0 && fab.isShown())
                    fab.hide();
            }

        });

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), EventCreateActivity.class);
            intent.putExtra("TYPE", EVENT);
            startActivity(intent);
        });

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

        calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date dateClicked) {
                scrollToDate(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                scrollToDate(firstDayOfNewMonth);
                setDateTitle(firstDayOfNewMonth);
            }

        });

        setDateTitle(new Date());
        scrollToDate(new Date());
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setUseThreeLetterAbbreviation(true);
        calendar.shouldDrawIndicatorsBelowSelectedDays(true);
    }

    private void setExpandAndCollapseEnabled(boolean enabled) {
        if (recyclerView.isNestedScrollingEnabled() != enabled)
            recyclerView.setNestedScrollingEnabled(enabled);
    }

    private int lastYear;

    private void setDateTitle(Date date) {
        LocalDate newDate = new LocalDate(date);
        LocalDate currDate = new LocalDate();

        if (currDate.getYear() != newDate.getYear()) {
            title.setText(year.format(date));

            if (newDate.getYear() != lastYear) {
                lastYear = newDate.getYear();

                String[] years = User.getYears();

                for (int i = 0; i < years.length; i++) {
                    String y = years[i];

                    if (y.contains(String.valueOf(lastYear))) {
                        Client.get().loadYear(i);
                    }
                }
            }
        } else {
            title.setText(month.format(date));
        }
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

        if (i >= 0) {
            offset = i;
            topSpace = 0;
            layout.scrollToPositionWithOffset(i, 0);
        }
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
        } else if (item.getItemId() == R.id.action_today) {
            scrollToDate(new Date());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart(int pg, int pos) {
        if (pg == PG_CALENDAR)
            refresh.setRefreshing(true);
    }

    @Override
    public void onFinish(int pg, int pos) {
        if (pg == PG_CALENDAR)
            refresh.setRefreshing(false);
    }

    @Override
    public void onError(int pg, String error) {
        if (pg == PG_CALENDAR)
            refresh.setRefreshing(false);
    }

    @Override
    public void onAccessDenied(int pg, String message) {
        if (pg == PG_CALENDAR)
            refresh.setRefreshing(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Client.get().addOnResponseListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Client.get().addOnResponseListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Client.get().removeOnResponseListener(this);
        refresh.setRefreshing(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Client.get().removeOnResponseListener(this);
        refresh.setRefreshing(false);
    }

}
