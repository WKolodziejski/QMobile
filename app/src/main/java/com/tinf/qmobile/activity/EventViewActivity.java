package com.tinf.qmobile.activity;

import static com.tinf.qmobile.model.ViewType.CLASS;
import static com.tinf.qmobile.model.ViewType.EVENT;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MESSAGE;
import static com.tinf.qmobile.model.ViewType.SCHEDULE;
import static com.tinf.qmobile.model.ViewType.USER;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.ActivityEventViewBinding;
import com.tinf.qmobile.fragment.view.ClassViewFragment;
import com.tinf.qmobile.fragment.view.SimpleEventViewFragment;
import com.tinf.qmobile.fragment.view.UserEventViewFragment;
import com.tinf.qmobile.fragment.view.JournalViewFragment;
import com.tinf.qmobile.fragment.view.MessageViewFragment;
import com.tinf.qmobile.fragment.view.ScheduleViewFragment;
import com.tinf.qmobile.utility.ColorsUtils;
import com.tinf.qmobile.utility.DesignUtils;

public class EventViewActivity extends AppCompatActivity {
  private ActivityEventViewBinding binding;

  @Override
  protected void onCreate(
      @Nullable
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ColorsUtils.setSystemBarColor(this, com.google.android.material.R.attr.colorSurface);
    binding = ActivityEventViewBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    setSupportActionBar(findViewById(R.id.toolbar_default));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeAsUpIndicator(
        DesignUtils.getDrawable(this, R.drawable.ic_cancel));

    Log.d("EventView", String.valueOf(getParent()));

    Bundle bundle = getIntent().getExtras();

    if (bundle == null) {
      finish();
      return;
    }

    Fragment fragment = null;

    switch (bundle.getInt("TYPE")) {

      case USER:
        fragment = new UserEventViewFragment();
        break;

      case JOURNAL:
        fragment = new JournalViewFragment();
        break;

      case SCHEDULE:
        fragment = new ScheduleViewFragment();
        break;

      case MESSAGE:
        fragment = new MessageViewFragment();
        break;

      case CLASS:
        fragment = new ClassViewFragment();
        break;

      case EVENT:
        fragment = new SimpleEventViewFragment();
        break;

      default:
        finish();
    }

    fragment.setArguments(bundle);

    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.event_view_fragment, fragment)
        .commit();

    setResult(RESULT_OK);
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
