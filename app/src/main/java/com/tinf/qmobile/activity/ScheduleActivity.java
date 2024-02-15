package com.tinf.qmobile.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.ActivityScheduleBinding;
import com.tinf.qmobile.fragment.ScheduleFragment;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;

public class ScheduleActivity extends AppCompatActivity implements OnResponse {
  private ActivityScheduleBinding binding;

  @Override
  protected void onCreate(
      @Nullable
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityScheduleBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    setSupportActionBar(findViewById(R.id.toolbar_default));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeAsUpIndicator(
        AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_cancel));

    Client.get().load(PG_SCHEDULE);

    ScheduleFragment fragment = new ScheduleFragment();
    fragment.setDaysLayout(binding.daysLayout);

    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.schedule_fragment, fragment)
        .commit();

    binding.refresh.setEnabled(false);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home)
      onBackPressed();

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onStart(int pg) {
    if (pg == PG_CALENDAR)
      binding.refresh.setRefreshing(true);
  }

  @Override
  public void onFinish(int pg, int year, int period) {
    if (pg == PG_SCHEDULE)
      binding.refresh.setRefreshing(false);
  }

  @Override
  public void onError(int pg, String error) {
    if (pg == PG_SCHEDULE)
      binding.refresh.setRefreshing(false);
  }

  @Override
  public void onAccessDenied(int pg, String message) {
    if (pg == PG_SCHEDULE)
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
