package com.tinf.qmobile.activity;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.ActivityEventViewBinding;
import com.tinf.qmobile.fragment.view.ClassViewFragment;
import com.tinf.qmobile.fragment.view.EventViewFragment;
import com.tinf.qmobile.fragment.view.JournalViewFragment;
import com.tinf.qmobile.fragment.view.MessageViewFragment;
import com.tinf.qmobile.fragment.view.ScheduleViewFragment;
import static com.tinf.qmobile.activity.EventCreateActivity.SCHEDULE;
import static com.tinf.qmobile.model.ViewType.CLASS;
import static com.tinf.qmobile.model.ViewType.JOURNAL;
import static com.tinf.qmobile.model.ViewType.MESSAGE;
import static com.tinf.qmobile.model.ViewType.USER;

public class EventViewActivity extends AppCompatActivity {
    private ActivityEventViewBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(findViewById(R.id.toolbar_default));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_cancel));

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            Fragment fragment = null;

            switch (bundle.getInt("TYPE")) {

                case USER:
                    fragment = new EventViewFragment();
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

                default: finish();
            }

            fragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.event_view_fragment, fragment)
                    .commit();

            setResult(RESULT_OK);
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
