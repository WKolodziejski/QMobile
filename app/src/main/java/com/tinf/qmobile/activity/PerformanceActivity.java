package com.tinf.qmobile.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.ActivityPerformanceBinding;
import com.tinf.qmobile.fragment.PerformanceFragment;
import com.tinf.qmobile.utility.ColorsUtils;
import com.tinf.qmobile.utility.DesignUtils;

public class PerformanceActivity extends AppCompatActivity {
  private ActivityPerformanceBinding binding;

  @Override
  public void onCreate(
      @Nullable
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ColorsUtils.setSystemBarColor(this, com.google.android.material.R.attr.colorSurface);
    binding = ActivityPerformanceBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    setSupportActionBar(findViewById(R.id.toolbar_default));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeAsUpIndicator(
        DesignUtils.getDrawable(this, R.drawable.ic_cancel));

    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.performance_fragment, new PerformanceFragment())
        .commit();

    //binding.refresh.setEnabled(false);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home)
      onBackPressed();

    return super.onOptionsItemSelected(item);
  }

}
