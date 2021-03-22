package com.tinf.qmobile.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tinf.qmobile.R;
import com.tinf.qmobile.fragment.ScheduleFragment;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.utility.User;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.network.OnResponse.PG_SCHEDULE;

public class ScheduleActivity extends AppCompatActivity implements OnResponse {
    @BindView(R.id.schedule_refresh)    SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ButterKnife.bind(this);
        setSupportActionBar(findViewById(R.id.toolbar_default));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_cancel));

        Client.get().load(PG_SCHEDULE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.schedule_fragment, new ScheduleFragment())
                .commit();

        refresh.setEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart(int pg, int pos) {
        if (pg == PG_CALENDAR)
            refresh.setRefreshing(true);
    }

    @Override
    public void onFinish(int pg, int pos) {
        if (pg == PG_SCHEDULE)
            refresh.setRefreshing(false);
    }

    @Override
    public void onError(int pg, String error) {
        if (pg == PG_SCHEDULE)
            refresh.setRefreshing(false);
    }

    @Override
    public void onAccessDenied(int pg, String message) {
        if (pg == PG_SCHEDULE)
            refresh.setRefreshing(false);
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
        refresh.setRefreshing(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Client.get().removeOnResponseListener(this);
        refresh.setRefreshing(false);
    }

}
