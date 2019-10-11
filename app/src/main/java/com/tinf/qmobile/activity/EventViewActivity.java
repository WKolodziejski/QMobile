package com.tinf.qmobile.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.tinf.qmobile.fragment.view.ScheduleViewFragment;
import com.tinf.qmobile.model.calendario.Base.CalendarBase;
import com.tinf.qmobile.fragment.view.EventViewFragment;
import com.tinf.qmobile.fragment.view.JournalViewFragment;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.R;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import static com.tinf.qmobile.activity.calendar.EventCreateActivity.SCHEDULE;

public class EventViewActivity extends AppCompatActivity {
    int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);

        setSupportActionBar(findViewById(R.id.toolbar_default));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_cancel));

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            /*if (bundle.getInt("ID") == 0) {
                finish();
            }*/

            type = bundle.getInt("TYPE");

            Fragment fragment = null;

            switch (type) {

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

            if (type == CalendarBase.ViewType.USER || type == SCHEDULE) {
                Client.get().requestUpdate();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
