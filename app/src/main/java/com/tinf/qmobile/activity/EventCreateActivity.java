package com.tinf.qmobile.activity;

import static com.tinf.qmobile.model.ViewType.EVENT;
import static com.tinf.qmobile.model.ViewType.SCHEDULE;

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
import com.tinf.qmobile.utility.ColorsUtils;
import com.tinf.qmobile.utility.DesignUtils;

public class EventCreateActivity extends AppCompatActivity {
  public ActivityEventCreateBinding binding;
  //private AlarmReceiver alarmReceiver;

  @Override
  protected void onCreate(
      @Nullable
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ColorsUtils.setSystemBarColor(this, com.google.android.material.R.attr.colorSurface);
    binding = ActivityEventCreateBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeAsUpIndicator(
        DesignUtils.getDrawable(this, R.drawable.ic_cancel));

    //alarmReceiver = new AlarmReceiver();

        /*AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            registerReceiver(alarmReceiver,
                    new IntentFilter(AlarmManager
                    .ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED));

            if (!alarmManager.canScheduleExactAlarms()) {
                startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
            }
        }*/

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

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmReceiver != null)
                unregisterReceiver(alarmReceiver);
        }
    }*/
}
