package com.tinf.qmobile.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.tinf.qmobile.Fragment.EventViewFragment;
import com.tinf.qmobile.R;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class EventViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);

        setSupportActionBar(findViewById(R.id.toolbar_default));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_cancel));

        Fragment fragment = new EventViewFragment();

        fragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.event_view_fragment, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
