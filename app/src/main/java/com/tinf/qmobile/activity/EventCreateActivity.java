package com.tinf.qmobile.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.ActivityEventCreateBinding;
import com.tinf.qmobile.fragment.create.EventCreateFragment;
import com.tinf.qmobile.fragment.create.ScheduleCreateFragment;

public class EventCreateActivity extends AppCompatActivity {
    public ActivityEventCreateBinding binding;
    public final static int EVENT = 1;
    public final static int SCHEDULE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_cancel));

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
        new MaterialAlertDialogBuilder(EventCreateActivity.this)
                .setTitle(getString(R.string.dialog_discart_changes_title))
                .setMessage(getString(R.string.dialog_discart_changes_txt))
                .setCancelable(true)
                .setNegativeButton(getString(R.string.dialog_discart), (dialogInterface, i) -> finish())
                .setPositiveButton(getString(R.string.dialog_keep_editing), null)
                .create()
                .show();
    }

}
