package com.tinf.qacademico.Activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.objectbox.BoxStore;

import android.view.Menu;
import android.view.MenuItem;

import com.tinf.qacademico.App;
import com.tinf.qacademico.Fragment.HorarioFragment;
import com.tinf.qacademico.R;
import com.tinf.qacademico.WebView.SingletonWebView;

import java.util.Objects;

import static com.tinf.qacademico.Utilities.Utils.PG_HORARIO;
import static com.tinf.qacademico.Utilities.Utils.URL;

public class HorarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario);

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
}
