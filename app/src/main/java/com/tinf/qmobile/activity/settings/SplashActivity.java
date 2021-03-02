package com.tinf.qmobile.activity.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.tinf.qmobile.activity.LoginActivity;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.service.Jobs;
import com.tinf.qmobile.utility.User;


import static com.tinf.qmobile.fragment.SettingsFragment.DATA;
import static com.tinf.qmobile.fragment.SettingsFragment.POPUP;
import static com.tinf.qmobile.utility.User.PASSWORD;
import static com.tinf.qmobile.utility.User.REGISTRATION;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;

        if (User.isValid()) {
            Jobs.scheduleJob(false);
            if (!Client.get().isValid()) {
                Client.get().login();
                Log.d(TAG, "Calling login");
            }

            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }

}
