package com.tinf.qmobile.activity.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.utility.User;

import java.util.Objects;

import static com.tinf.qmobile.activity.settings.SettingsActivity.NIGHT;

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
