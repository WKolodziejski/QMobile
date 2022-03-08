package com.tinf.qmobile.activity;

import static com.tinf.qmobile.model.ViewType.EVENT;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.kodmap.library.kmrecyclerviewstickyheader.KmHeaderItemDecoration;
import com.tinf.qmobile.R;
import com.tinf.qmobile.adapter.CalendarAdapter2;
import com.tinf.qmobile.databinding.ActivityCalendarBinding;
import com.tinf.qmobile.model.calendar.CalendarBase;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.utility.UserUtils;
import com.tinf.qmobile.widget.calendar.CalendarRecyclerView;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity implements CalendarRecyclerView.AppBarTracking, OnResponse {
    private ActivityCalendarBinding binding;

    private int appBarOffset = 0;
    private int offset = 0;
    private int topSpace = 0;
    private boolean isExpanded = false;
    private boolean isIdle = true;
    private LinearLayoutManager layout;
    private CalendarAdapter2 adapter;

    private static final SimpleDateFormat month = new SimpleDateFormat("MMMM", Locale.getDefault());
    private static final SimpleDateFormat year = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_cancel));

        Date today = new Date();

        Client.get().load(PG_CALENDAR);

        layout = new LinearLayoutManager(this);
        adapter = new CalendarAdapter2(this, binding.calendar, () -> scrollToDate(today));

        binding.refresh.setEnabled(false);

        binding.recycler.setAppBarTracking(this);
        binding.recycler.setItemViewCacheSize(20);
        binding.recycler.setDrawingCacheEnabled(true);
        binding.recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.recycler.setLayoutManager(layout);
        binding.recycler.setAdapter(adapter);
        binding.recycler.addItemDecoration(new KmHeaderItemDecoration(adapter));
        binding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!isExpanded && newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
                    binding.calendar.setCurrentDate(adapter.getList().get(layout.findFirstVisibleItemPosition()).getDate());
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                setDateTitle(adapter.getList().get(layout.findFirstVisibleItemPosition()).getDate());

                if (dy < 0 && !binding.fab.isShown())
                    binding.fab.show();
                else if(dy > 0 && binding.fab.isShown())
                    binding.fab.hide();
            }

        });

        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), EventCreateActivity.class);
            intent.putExtra("TYPE", EVENT);
            startActivity(intent);
        });

        binding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (appBarOffset != verticalOffset) {
                appBarOffset = verticalOffset;
                int totalScrollRange = appBarLayout.getTotalScrollRange();
                float progress = (float) (-verticalOffset) / (float) totalScrollRange;
                binding.arrow.setRotation(-progress * 180);
                isExpanded = verticalOffset == 0;
                isIdle = true;

                if (appBarOffset == -appBarLayout.getTotalScrollRange()) {
                    isExpanded = false;
                    setExpandAndCollapseEnabled(false);
                } else {
                    setExpandAndCollapseEnabled(true);
                }

                if (appBarOffset == 0) {
                    if (isExpanded) {
                        binding.recycler.stopScroll();
                        isExpanded = true;
                        offset = layout.findFirstVisibleItemPosition();
                        topSpace = layout.findViewByPosition(layout.findFirstVisibleItemPosition()).getTop();
                    }
                }
            }
        });

        binding.expand.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            binding.recycler.stopScroll();
            binding.appbar.setExpanded(isExpanded, true);
        });

        binding.calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {

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

        setDateTitle(today);
        //scrollToDate(today);
        binding.calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        binding.calendar.setUseThreeLetterAbbreviation(true);
        binding.calendar.shouldDrawIndicatorsBelowSelectedDays(true);
    }

    private void setExpandAndCollapseEnabled(boolean enabled) {
        if (binding.recycler.isNestedScrollingEnabled() != enabled)
            binding.recycler.setNestedScrollingEnabled(enabled);
    }

    private int lastYear;

    private void setDateTitle(Date date) {
        LocalDate newDate = new LocalDate(date);
        LocalDate currDate = new LocalDate();

        if (currDate.getYear() != newDate.getYear()) {
            binding.title.setText(year.format(date));

            if (newDate.getYear() != lastYear) {
                lastYear = newDate.getYear();

                String[] years = UserUtils.getYears();

                for (int i = 0; i < years.length; i++) {
                    String y = years[i];

                    if (y.contains(String.valueOf(lastYear))) {
                        Client.get().loadYear(i);
                    }
                }
            }
        } else {
            binding.title.setText(month.format(date));
        }
    }

    private void scrollToDate(Date key) {
        List<CalendarBase> array = adapter.getList();
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

            else break;
        }

        if (i >= 0) {
            offset = i;
            topSpace = 0;

            if (i - 1 >= 0 && !isExpanded)
                i -= 1;

            layout.scrollToPositionWithOffset(i, 0);
            binding.fab.show();
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
    public void onStart(int pg) {
        if (pg == PG_CALENDAR || pg == PG_CLASSES)
            binding.refresh.setRefreshing(true);
    }

    @Override
    public void onFinish(int pg) {
        if (pg == PG_CALENDAR || pg == PG_CLASSES)
            binding.refresh.setRefreshing(false);
    }

    @Override
    public void onError(int pg, String error) {
        if (pg == PG_CALENDAR || pg == PG_CLASSES)
            binding.refresh.setRefreshing(false);
    }

    @Override
    public void onAccessDenied(int pg, String message) {
        if (pg == PG_CALENDAR || pg == PG_CLASSES)
            binding.refresh.setRefreshing(false);
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
        binding.refresh.setRefreshing(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Client.get().removeOnResponseListener(this);
        binding.refresh.setRefreshing(false);
    }

}
