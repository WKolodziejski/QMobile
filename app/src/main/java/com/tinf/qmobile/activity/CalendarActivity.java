package com.tinf.qmobile.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.tinf.qmobile.R;
import com.tinf.qmobile.fragment.CalendarFragment;
import com.tinf.qmobile.network.Client;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tinf.qmobile.network.OnResponse.PG_CALENDARIO;


public class CalendarActivity extends AppCompatActivity {
    @BindView(R.id.compactcalendar_view) public CompactCalendarView calendar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar_calendar));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.calendario_fragment, new CalendarFragment())
                .commit();
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
            Client.get().load(PG_CALENDARIO);
            return true;
        } else if (item.getItemId() == R.id.action_today) {
            Client.get().requestScroll();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
