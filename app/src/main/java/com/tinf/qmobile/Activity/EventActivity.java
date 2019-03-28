package com.tinf.qmobile.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.tinf.qmobile.Fragment.EventFragment;
import com.tinf.qmobile.R;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EventActivity extends AppCompatActivity {
    @BindView(R.id.event_add)   public Button add;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar_events));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_cancel));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.event_fragment, new EventFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(EventActivity.this)
                .setTitle(getString(R.string.dialog_discart_changes_title))
                .setMessage(getString(R.string.dialog_discart_changes_txt))
                .setCancelable(true)
                .setNegativeButton(getString(R.string.dialog_discart), (dialogInterface, i) -> finish())
                .setPositiveButton(getString(R.string.dialog_keep_editing), null)
                .create()
                .show();
    }
}
