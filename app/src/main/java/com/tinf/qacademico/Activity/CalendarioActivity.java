package com.tinf.qacademico.Activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.tinf.qacademico.App;
import com.tinf.qacademico.Fragment.CalendarioFragment;
import com.tinf.qacademico.Interfaces.Fragments.OnUpdate;
import com.tinf.qacademico.Interfaces.WebView.OnPageLoad;
import com.tinf.qacademico.R;
import com.tinf.qacademico.WebView.SingletonWebView;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.objectbox.BoxStore;

import static com.tinf.qacademico.Utilities.Utils.PG_CALENDARIO;
import static com.tinf.qacademico.Utilities.Utils.URL;

public class CalendarioActivity extends AppCompatActivity implements OnPageLoad.Main {
    @BindView(R.id.compactcalendar_view) public CompactCalendarView calendar;
    @BindView(R.id.progressbar_horizontal_calendar)      SmoothProgressBar progressBar;
    private SingletonWebView webView = SingletonWebView.getInstance();
    private OnUpdate onUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar_calendario));
       // Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_calendario);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.calendario_fragment, new CalendarioFragment())
                .commit();
    }

    @Override
    public void setTitle(CharSequence text) {
        TextView title = (TextView) findViewById(R.id.actionBar_title);
        title.setText(text);
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
            webView.loadUrl(URL + PG_CALENDARIO);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public BoxStore getBox() {
        return ((App) getApplication()).getBoxStore();
    }

    public void setOnUpdateListener(OnUpdate onUpdate){
        this.onUpdate = onUpdate;
    }

    @Override
    public void onPageStart() {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.progressiveStart();
        });
    }

    @Override
    public void onPageFinish(String url_p) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            progressBar.progressiveStop();
            if (onUpdate != null) {
                onUpdate.onUpdate(url_p);
            }
        });
    }

    @Override
    public void onErrorRecived(String error) {}

    @Override
    public void onStart() {
        super.onStart();
        webView.setOnPageLoadListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.setOnPageLoadListener(this);
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
}
