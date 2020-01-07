package com.tinf.qmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.tinf.qmobile.R;
import com.tinf.qmobile.activity.calendar.EventCreateActivity;
import com.tinf.qmobile.fragment.ScheduleFragment;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.utility.User;

import java.util.Objects;

import butterknife.ButterKnife;

import static com.tinf.qmobile.activity.calendar.EventCreateActivity.SCHEDULE;
import static com.tinf.qmobile.network.Client.pos;
import static com.tinf.qmobile.network.OnResponse.PG_HORARIO;

public class HorarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario);

        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar_default));

        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.title_horario)
                + " ― " + User.getYears()[pos]);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.schedule_fragment, new ScheduleFragment())
                .commit();

        ExtendedFloatingActionButton fab = (ExtendedFloatingActionButton) findViewById(R.id.fab_add_schedule);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EventCreateActivity.class);
            intent.putExtra("TYPE", SCHEDULE);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_refresh) {
            Client.get().load(PG_HORARIO);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
