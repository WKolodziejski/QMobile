package com.tinf.qmobile.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.tinf.qmobile.BuildConfig;
import com.tinf.qmobile.activity.LoginActivity;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.service.Works;
import com.tinf.qmobile.utility.User;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SplashScreen.installSplashScreen(this).setKeepOnScreenCondition(() -> true);

        Intent intent;

        if (User.isValid()) {
            Works.schedule(false);

            if (!Client.get().isValid() && !BuildConfig.DEBUG)
                Client.get().login();

            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }

}
