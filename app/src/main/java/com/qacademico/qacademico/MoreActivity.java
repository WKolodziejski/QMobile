package com.qacademico.qacademico;

import android.content.Context;
import android.graphics.PorterDuff;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

public class MoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        ImageView tinf_logo = (ImageView) findViewById(R.id.tinf_logo);
        tinf_logo.setOnLongClickListener(v -> {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
            Toast.makeText(getApplicationContext(), "\ud83d\udc03", Toast.LENGTH_SHORT).show();
            return true;
        });
    }
}
