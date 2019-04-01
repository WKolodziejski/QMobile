package com.tinf.qmobile.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.tinf.qmobile.Activity.Calendar.EventCreateActivity;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Calendario.Base.CalendarBase;
import com.tinf.qmobile.Class.Calendario.EventJournal;
import com.tinf.qmobile.Class.Calendario.EventUser;
import com.tinf.qmobile.Fragment.EventViewFragment;
import com.tinf.qmobile.Fragment.JournalViewFragment;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.R;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class EventViewActivity extends AppCompatActivity {
    @CalendarBase.ViewType int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);

        setSupportActionBar(findViewById(R.id.toolbar_default));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_cancel));

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            type = bundle.getInt("TYPE");

            Fragment fragment = null;

            switch (type) {

                case CalendarBase.ViewType.USER:
                    fragment = new EventViewFragment();
                    break;

                case CalendarBase.ViewType.JOURNAL:
                    fragment = new JournalViewFragment();
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

            if (type == CalendarBase.ViewType.JOURNAL) {
                Client.get().requestUpdate();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
