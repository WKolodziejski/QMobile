package com.tinf.qmobile.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.tinf.qmobile.fragment.MateriaFragment;
import com.tinf.qmobile.R;

public class MateriaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materia);

        setSupportActionBar(findViewById(R.id.toolbar_default));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_cancel));

        Bundle bundle = getIntent().getExtras();

        Fragment fragment = new MateriaFragment();
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.materia_fragment, fragment)
                .commit();
    }

    private void changeColor() {
        ColorPickerDialogBuilder
                .with(getApplicationContext())
                .setTitle("Choose color")
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {

                    //materias.get(X).setColor(color);
                })
                .build()
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
