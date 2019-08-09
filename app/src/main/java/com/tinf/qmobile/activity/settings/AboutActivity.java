package com.tinf.qmobile.activity.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tinf.qmobile.R;
import com.tinf.qmobile.utility.User;

import static com.tinf.qmobile.activity.settings.SettingsActivity.NIGHT;

public class AboutActivity extends AppCompatActivity {
    int counter = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String version = "";

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView version_txt = (TextView) findViewById(R.id.about_version);
        version_txt.append(" " + version);

        ImageView icon = (ImageView) findViewById(R.id.about_icon);
        icon.setOnClickListener(view -> {
            if (User.isNight()) {
                Toast.makeText(getApplicationContext(), "Modo já noturno desbloqueado.", Toast.LENGTH_LONG).show();
            } else {
                if (counter > 0) {
                    Toast.makeText(getApplicationContext(), counter + " restantes.", Toast.LENGTH_LONG).show();
                    counter--;
                } else {
                    Toast.makeText(getApplicationContext(), "Modo noturno (beta) desbloqueado.", Toast.LENGTH_LONG).show();

                    User.setNight(true);

                    SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    prefs.putBoolean(NIGHT, true);
                    prefs.apply();

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }
        });


    }

    public void more(View v){
        startActivity(new Intent(getApplicationContext(), MoreActivity.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
