package com.tinf.qacademico.Activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.objectbox.BoxStore;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tinf.qacademico.App;
import com.tinf.qacademico.Fragment.HorarioFragment;
import com.tinf.qacademico.Interfaces.Fragments.OnUpdate;
import com.tinf.qacademico.Interfaces.WebView.OnPageLoad;
import com.tinf.qacademico.R;
import com.tinf.qacademico.WebView.SingletonWebView;

import java.util.Objects;

import static com.tinf.qacademico.Utilities.Utils.PG_HORARIO;
import static com.tinf.qacademico.Utilities.Utils.URL;

public class HorarioActivity extends AppCompatActivity implements OnPageLoad, OnPageLoad.Main {
    @BindView(R.id.progressbar_horizontal) SmoothProgressBar progressBar;
    private SingletonWebView webView = SingletonWebView.getInstance();
    private OnUpdate onUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario);

        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar));

        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.title_horario)
                + " ― " + webView.data_year[webView.year_position]);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.horario_fragment, new HorarioFragment())
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
            SingletonWebView.getInstance().loadUrl(URL + PG_HORARIO);
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
            onUpdate.onUpdate();
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
}
