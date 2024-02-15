package com.tinf.qmobile.activity.settings;

import static com.tinf.qmobile.App.USE_COUNT;
import static com.tinf.qmobile.App.USE_INFO;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;
import androidx.preference.PreferenceManager;

import com.tinf.qmobile.BuildConfig;
import com.tinf.qmobile.activity.LoginActivity;
import com.tinf.qmobile.activity.MainActivity;
import com.tinf.qmobile.database.DataBase;
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

    // Se login foi validado e campus foi selecionado (necessário para buscar nova url no Firebase)
    if (UserUtils.isValid() && UserUtils.getCampus() != null) {
      Works.scheduleParser();

      if (!Client.get()
                 .isValid() && !BuildConfig.DEBUG)
        Client.get()
              .login();

      getSharedPreferences(USE_INFO, MODE_PRIVATE).edit()
                                                  .putInt(USE_COUNT,
                                                          getSharedPreferences(USE_INFO,
                                                                               MODE_PRIVATE).getInt(
                                                              USE_COUNT, 0) + 1)
                                                  .apply();

      intent = new Intent(this, MainActivity.class);
    } else {
      Client.get()
            .close();
      Works.cancelAll();
      DataBase.get()
              .close();
      UserUtils.clearInfo();
      PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                       .edit()
                       .clear()
                       .apply();
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

      intent = new Intent(this, LoginActivity.class);
    }

    startActivity(intent);
    finish();
  }

}
