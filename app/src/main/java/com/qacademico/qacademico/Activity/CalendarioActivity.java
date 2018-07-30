package com.qacademico.qacademico.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.qacademico.qacademico.Fragment.CalendarioFragment;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;

import java.util.List;
import java.util.Objects;

public class CalendarioActivity extends AppCompatActivity implements SingletonWebView.OnPageFinished {
    public SingletonWebView mainWebView = SingletonWebView.getInstance();
    CalendarioFragment calendarioFragment = new CalendarioFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_calendario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainWebView.setOnPageFinishedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.calendario_fragment, calendarioFragment, Utils.CALENDARIO).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageFinish(String url_p, List<?> list) {
        runOnUiThread(() -> {

        });
    }
}
