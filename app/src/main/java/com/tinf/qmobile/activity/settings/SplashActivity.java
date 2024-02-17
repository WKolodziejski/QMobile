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

import java.util.Map;

public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ColorsUtils.setSystemBarColor(this, com.google.android.material.R.attr.colorSurface);

    SplashScreen.installSplashScreen(this)
                .setKeepOnScreenCondition(() -> true);

    // Se o usuário não estiver logado, abre a tela de login
    if (!UserUtils.isValid()) {
      startActivity(new Intent(this, LoginActivity.class));
      finish();
      return;
    }

    Works.scheduleParser();

    if (!Client.get().isValid())
      Client.get().login();

    incrementLoginCount();
    fillMissingData();

    Intent intent = UserUtils.getWebapp() ? new Intent(this,
                                                       WebAppActivity.class)
                                          : new Intent(this,
                                                       MainActivity.class);

    startActivity(intent);
    finish();
  }

  private void incrementLoginCount() {
    getSharedPreferences(USE_INFO, MODE_PRIVATE).edit().putInt(USE_COUNT,
                                                               getSharedPreferences(USE_INFO,
                                                                                    MODE_PRIVATE).getInt(
                                                                   USE_COUNT, 0) + 1).apply();
  }

  // Se o campus não estiver definido, busca pela url
  private void fillMissingData() {
    if (UserUtils.getCampus() != null)
      return;

    Map<String, String> urls = UserUtils.getUrlMap();
    String userUrl = UserUtils.getURL();
    String campus = null;

    // Busca url no mapa
    for (Map.Entry<String, String> entry : urls.entrySet()) {
      if (entry.getValue().equals(userUrl)) {
        campus = entry.getKey();
        break;
      }
    }

    if (campus == null)
      return;

    // Define se o campus deve usar versão webapp
    boolean webapp = UserUtils.getWebappList().contains(campus);

    UserUtils.setCampus(campus);
    UserUtils.setWebapp(webapp);
  }

}
