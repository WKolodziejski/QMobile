package com.tinf.qacademico.Activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.tinf.qacademico.Fragment.CalendarioFragment;
import com.tinf.qacademico.R;

import java.util.Date;

public class CalendarioActivity2 extends AppCompatActivity {
    public CompactCalendarView calendar;
    private AppBarLayout appBarLayout;
    private boolean isExpanded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appBarLayout = findViewById(R.id.app_bar_layout);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.calendario_fragment, new CalendarioFragment())
                .commit();

        calendar = findViewById(R.id.compactcalendar_view);

        calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date dateClicked) {

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
            }
        });

        // Set current date to today
        setCurrentDate(new Date());

        RelativeLayout datePickerButton = findViewById(R.id.date_picker_button);

        datePickerButton.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            appBarLayout.setExpanded(isExpanded, true);
        });
    }

    private void setCurrentDate(Date date) {
        if (calendar != null) {
            calendar.setCurrentDate(date);
        }
    }

    @Override
    public void setTitle(CharSequence text) {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(text);
    }
}
