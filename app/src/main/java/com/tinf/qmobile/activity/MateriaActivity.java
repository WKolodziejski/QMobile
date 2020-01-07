package com.tinf.qmobile.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.fragment.matter.TabsAdapter;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.network.Client;

public class MateriaActivity extends AppCompatActivity {
    private Matter matter;
    private boolean changed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materia);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
            matter = App.getBox().boxFor(Matter.class).get(bundle.getLong("ID"));
        else
            finish();

        setSupportActionBar(findViewById(R.id.toolbar_matter));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_cancel));
        getSupportActionBar().setTitle(matter.getTitle());

        TabsAdapter adapter = new TabsAdapter(getApplicationContext(), getSupportFragmentManager(), getIntent().getExtras());

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager_matter);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_matter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

        } else if (item.getItemId() == R.id.action_color) {
            if (matter != null) {
                ColorPickerDialogBuilder
                        .with(this)
                        .setTitle(getString(R.string.dialog_choose_color))
                        .initialColor(matter.getColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                        .density(6)
                        .lightnessSliderOnly()
                        .setPositiveButton(getString(R.string.dialog_select), (dialog, selectedColor, allColors) -> {
                            matter.setColor(selectedColor);
                            App.getBox().boxFor(Matter.class).put(matter);
                            changed = true;
                            Client.get().requestUpdate();
                        })
                        .setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> {})
                        .build()
                        .show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.matters, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (changed)
            Client.get().requestUpdate();
    }

}
