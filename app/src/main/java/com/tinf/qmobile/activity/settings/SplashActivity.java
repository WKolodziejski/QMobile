package com.tinf.qmobile.activity.settings;

import static com.tinf.qmobile.App.USE_COUNT;
import static com.tinf.qmobile.App.USE_INFO;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.tinf.qmobile.activity.LoginActivity;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.activity.WebAppActivity;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.service.Works;
import com.tinf.qmobile.utility.ColorsUtils;
import com.tinf.qmobile.utility.UserUtils;

public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ColorsUtils.setSystemBarColor(this, com.google.android.material.R.attr.colorSurface);

    SplashScreen.installSplashScreen(this)
                .setKeepOnScreenCondition(() -> true);

    Intent intent;

    if (UserUtils.isValid()) {
      Works.scheduleParser();

      if (!Client.get().isValid())
        Client.get().login();

      getSharedPreferences(USE_INFO, MODE_PRIVATE).edit().putInt(USE_COUNT,
                                                                 getSharedPreferences(USE_INFO,
                                                                                      MODE_PRIVATE).getInt(
                                                                     USE_COUNT, 0) + 1).apply();
//      TODO: implementar
      if (false) {
        intent = new Intent(this, WebAppActivity.class);
      } else {
        intent = new Intent(this, MainActivity.class);
      }
    } else {
      intent = new Intent(this, LoginActivity.class);
    }

    startActivity(intent);
    finish();
  }

}
