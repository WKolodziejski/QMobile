package com.tinf.qmobile.activity.calendar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.tinf.qmobile.fragment.EventCreateFragment;
import com.tinf.qmobile.fragment.ScheduleCreateFragment;
import com.tinf.qmobile.R;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EventCreateActivity extends AppCompatActivity {
    public final static int EVENT = 1;
    public final static int SCHEDULE = 2;
    @BindView(R.id.event_add) public Button add;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar_events));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_cancel));

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            Fragment fragment = null;

            switch (bundle.getInt("TYPE")) {

                case EVENT:
                    fragment = new EventCreateFragment();
                    break;

                case SCHEDULE:
                    fragment = new ScheduleCreateFragment();
                    break;

                default:
                    finish();
            }

            fragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.event_create_fragment, fragment)
                    .commit();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(EventCreateActivity.this)
                .setTitle(getString(R.string.dialog_discart_changes_title))
                .setMessage(getString(R.string.dialog_discart_changes_txt))
                .setCancelable(true)
                .setNegativeButton(getString(R.string.dialog_discart), (dialogInterface, i) -> finish())
                .setPositiveButton(getString(R.string.dialog_keep_editing), null)
                .create()
                .show();
    }

}
