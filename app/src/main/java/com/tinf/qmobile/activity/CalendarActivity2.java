package com.tinf.qmobile.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tinf.qmobile.R;
import com.tinf.qmobile.widget.CalendarWidget;

import org.joda.time.LocalDate;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CalendarActivity2 extends AppCompatActivity {
    @BindView(R.id.nestedView)      RecyclerView recycler;
    @BindView(R.id.calendarView)    CalendarWidget calendarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar2);
        ButterKnife.bind(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CALENDAR},200);
            }
        } else {
            LocalDate minTime = new LocalDate().minusYears(10);
            LocalDate maxTime = new LocalDate().plusYears(10);
            HashMap<LocalDate, String[]> events = new HashMap<>();
            String[] s = {"TESTE"};
            events.put(new LocalDate(), s);
            //calendarView.init(events, minTime, maxTime);
            //calendarView.setMonth(new LocalDate());
        }
    }


}
