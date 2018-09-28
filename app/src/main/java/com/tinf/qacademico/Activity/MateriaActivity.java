package com.tinf.qacademico.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.tinf.qacademico.R;

public class MateriaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materia);


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
}
