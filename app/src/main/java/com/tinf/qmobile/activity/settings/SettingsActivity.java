package com.tinf.qmobile.activity.settings;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.tinf.qmobile.R;
import com.tinf.qmobile.fragment.SettingsFragment;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.service.Works;
import com.tinf.qmobile.utility.ColorsUtils;

public class SettingsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ColorsUtils.setSystemBarColor(this, com.google.android.material.R.attr.colorSurface);
    setContentView(R.layout.activity_settings);

    setSupportActionBar(findViewById(R.id.toolbar_default));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.settings_fragment, new SettingsFragment())
        .commit();
  }

  @Override
  public void finish() {
    super.finish();
    Works.scheduleParser();
    Client.get().requestDelayedUpdate();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
    }
    return super.onOptionsItemSelected(item);
  }

}