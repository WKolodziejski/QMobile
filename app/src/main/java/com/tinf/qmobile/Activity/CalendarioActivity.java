package com.tinf.qmobile.Activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.tinf.qmobile.Fragment.CalendarioFragment;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.Network.OnResponse;
import com.tinf.qmobile.Fragment.OnUpdate;
import com.tinf.qmobile.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CalendarioActivity extends AppCompatActivity implements OnResponse {
    @BindView(R.id.compactcalendar_view) public CompactCalendarView calendar;
    private OnUpdate onUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar_calendario));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.calendario_fragment, new CalendarioFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_refresh) {
            Client.get().load(PG_CALENDARIO);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setOnUpdateListener(OnUpdate onUpdate){
        this.onUpdate = onUpdate;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        onUpdate = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        onUpdate = null;
    }


    @Override
    public void onStart(int pg, int pos) {
    }

    @Override
    public void onFinish(int pg, int pos) {
        if (onUpdate != null) {
            onUpdate.onUpdate(pg);
        }
    }

    @Override
    public void onError(int pg, String error) {

    }

    @Override
    public void onAccessDenied(int pg, String message) {

    }
}
