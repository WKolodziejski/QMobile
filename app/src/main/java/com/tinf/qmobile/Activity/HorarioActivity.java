package com.tinf.qmobile.Activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;

import android.view.Menu;
import android.view.MenuItem;

import com.tinf.qmobile.Fragment.HorarioFragment;
import com.tinf.qmobile.Interfaces.OnResponse;
import com.tinf.qmobile.Interfaces.OnUpdate;
import com.tinf.qmobile.Interfaces.OnMateriaisLoad;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.R;

import java.util.Objects;


public class HorarioActivity extends AppCompatActivity implements OnResponse {
    //@BindView(R.id.progressbar_horizontal) SmoothProgressBar progressBar
    private OnUpdate onUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario);

        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar));

        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.title_horario)
                + " ― " + Client.getYear());
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
            //webView.loadUrl(URL + PG_HORARIO);
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
        //webView.setOnPageLoadListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //webView.setOnPageLoadListener(this);
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
    public void onStart(int pg, int year) {

    }

    @Override
    public void onFinish(int pg, int year) {
        runOnUiThread(() -> {
            //progressBar.setVisibility(View.GONE);
            //progressBar.progressiveStop();
            if (onUpdate != null) {
                onUpdate.onUpdate(pg);
            }
        });
    }

    @Override
    public void onError(int pg, String error) {
        runOnUiThread(() -> {
            //progressBar.setVisibility(View.VISIBLE);
            //progressBar.progressiveStart();
        });
    }

    @Override
    public void onAccessDenied(int pg, String message) {

    }
}
