package com.tinf.qacademico.Activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.objectbox.BoxStore;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tinf.qacademico.App;
import com.tinf.qacademico.Fragment.HorarioFragment;
import com.tinf.qacademico.R;
import com.tinf.qacademico.WebView.SingletonWebView;

import java.util.List;
import java.util.Objects;

import static com.tinf.qacademico.Utilities.Utils.PG_HORARIO;
import static com.tinf.qacademico.Utilities.Utils.URL;

public class HorarioActivity extends AppCompatActivity implements SingletonWebView.OnPageFinished, SingletonWebView.OnPageStarted {
    @BindView(R.id.progressbar_horizontal) SmoothProgressBar progressBar;
    private SingletonWebView webView = SingletonWebView.getInstance();
    private OnPageUpdated onPageUpdated;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario);

        ButterKnife.bind(this);

        webView.setOnPageFinishedListener(this);
        webView.setOnPageStartedListener(this);

        SingletonWebView webView = SingletonWebView.getInstance();

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

    @Override
    public void onPageFinish(String url_p, List<?> list) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            progressBar.progressiveStop();
            onPageUpdated.onPageUpdate();
        });
    }

    @Override
    public void onPageStart(String url_p) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.progressiveStart();
        });
    }

    public void setOnPageUpdateListener(OnPageUpdated onPageUpdated){
        this.onPageUpdated = onPageUpdated;
    }

    public interface OnPageUpdated {
        void onPageUpdate();
    }
}
