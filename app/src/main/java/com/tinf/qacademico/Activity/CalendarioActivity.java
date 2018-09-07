package com.tinf.qacademico.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.tinf.qacademico.Fragment.CalendarioFragment;
import com.tinf.qacademico.R;
import com.tinf.qacademico.WebView.SingletonWebView;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CalendarioActivity extends AppCompatActivity {
    @BindView(R.id.compactcalendar_view) public CompactCalendarView calendar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_calendario);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_calendario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.calendario_fragment, new CalendarioFragment())
                .commit();
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        calendar.showCalendarWithAnimation();
    }

    @Override
    public void setTitle(CharSequence text) {
        TextView title = (TextView) findViewById(R.id.actionBar_title);
        title.setText(text);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            calendar.hideCalendarWithAnimation();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
