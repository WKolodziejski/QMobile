package com.tinf.qmobile.activity.settings;

import static com.tinf.qmobile.App.USE_COUNT;
import static com.tinf.qmobile.App.USE_INFO;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.tinf.qmobile.BuildConfig;
import com.tinf.qmobile.activity.LoginActivity;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.service.Works;
import com.tinf.qmobile.utility.UserUtils;

public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SplashScreen.installSplashScreen(this).setKeepOnScreenCondition(() -> true);

    Intent intent;

    if (UserUtils.isValid()) {
      Works.scheduleParser();

      if (!Client.get().isValid() && !BuildConfig.DEBUG)
        Client.get().login();

      getSharedPreferences(USE_INFO, MODE_PRIVATE).edit().putInt(USE_COUNT,
                                                                 getSharedPreferences(USE_INFO,
                                                                                      MODE_PRIVATE).getInt(
                                                                     USE_COUNT, 0) + 1).apply();

      intent = new Intent(this, MainActivity.class);
    } else {
      intent = new Intent(this, LoginActivity.class);
    }

    startActivity(intent);
    finish();
  }

}
