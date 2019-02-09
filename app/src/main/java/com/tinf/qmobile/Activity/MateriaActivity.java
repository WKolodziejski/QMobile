package com.tinf.qmobile.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import io.objectbox.BoxStore;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.Fragment.MateriaFragment;
import com.tinf.qmobile.R;

import java.util.Objects;

public class MateriaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materia);

        setSupportActionBar(findViewById(R.id.toolbar_materia));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            int year = bundle.getInt("YEAR");
            String name = bundle.getString("NAME");

            Materia materia = App.getBox().boxFor(Materia.class).query().equal(Materia_.year, year)
                    .and().equal(Materia_.name, name).build().findFirst();

            if (materia != null) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(materia.getName());

                Fragment fragment = new MateriaFragment();
                fragment.setArguments(bundle);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.materia_fragment, fragment)
                        .commit();
            } else {
                finish();
            }
        } else {
            finish();
        }
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

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }*/

}
