package com.qacademico.qacademico.Activity.Settings;

import android.content.Context;
import android.graphics.PorterDuff;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qacademico.qacademico.R;

import java.util.Objects;

public class MoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        ImageView tinf_logo = (ImageView) findViewById(R.id.tinf_logo);
        tinf_logo.setOnLongClickListener(v -> {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            Objects.requireNonNull(vibrator).vibrate(1000);

            Toast toast = Toast.makeText(getApplicationContext(), "\ud83d\udc03", Toast.LENGTH_SHORT);
            ViewGroup group = (ViewGroup) toast.getView();
            TextView messageTextView = (TextView) group.getChildAt(0);
            messageTextView.setTextSize(35);
            toast.show();

            return true;
        });
    }
}