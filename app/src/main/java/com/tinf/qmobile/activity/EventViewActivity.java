package com.tinf.qmobile.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.tinf.qmobile.R;
import com.tinf.qmobile.fragment.view.EventViewFragment;
import com.tinf.qmobile.fragment.view.JournalViewFragment;
import com.tinf.qmobile.fragment.view.ScheduleViewFragment;
import com.tinf.qmobile.model.calendar.CalendarBase;

import static com.tinf.qmobile.activity.EventCreateActivity.SCHEDULE;

public class EventViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);

        Log.d("Fragment", "Initialized");

        setSupportActionBar(findViewById(R.id.toolbar_default));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_cancel));

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            Fragment fragment = null;

            switch (bundle.getInt("TYPE")) {

                case CalendarBase.ViewType.USER:
                    fragment = new EventViewFragment();
                    break;

                case CalendarBase.ViewType.JOURNAL:
                    fragment = new JournalViewFragment();
                    break;

                case SCHEDULE:
                    fragment = new ScheduleViewFragment();
                    break;

                default: finish();
            }

            fragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.event_view_fragment, fragment)
                    .commit();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
