package com.tinf.qmobile.activity.calendar;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.tinf.qmobile.fragment.CalendarioFragment;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tinf.qmobile.network.OnResponse.PG_CALENDARIO;


public class CalendarioActivity extends AppCompatActivity {
    @BindView(R.id.compactcalendar_view) public CompactCalendarView calendar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar_calendario));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.calendario_fragment, new CalendarioFragment())
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Client.get().requestUpdate();
    }

}
