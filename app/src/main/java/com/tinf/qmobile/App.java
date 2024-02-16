package com.tinf.qmobile;

import static com.tinf.qmobile.fragment.SettingsFragment.NIGHT;
import static com.tinf.qmobile.utility.UserUtils.PASSWORD;
import static com.tinf.qmobile.utility.UserUtils.REGISTRATION;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tinf.qmobile.utility.UserUtils;

import java.util.Map;

public class App extends Application {
  public static final String USE_INFO = ".use";
  public static final String USE_COUNT = ".count";
  public static final String USE_RATED = ".rated";
  private static Context context;

  @Override
  public void onCreate() {
    super.onCreate();
    context = getBaseContext();

//    TODO: habilitar
//    DynamicColors.applyToActivitiesIfAvailable(this);

    FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
    crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);
    crashlytics.setCustomKey("Register", UserUtils.getCredential(REGISTRATION));
    crashlytics.setCustomKey("Password", UserUtils.getCredential(PASSWORD));
    crashlytics.setCustomKey("URL", UserUtils.getURL());

    FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

    // Busca as configurações do Firebase Remote Config a cada 12h
    remoteConfig
        .setConfigSettingsAsync(new FirebaseRemoteConfigSettings
            .Builder()
                                    .setMinimumFetchIntervalInSeconds(43200) // 12h
                                    .build())
        .addOnFailureListener(crashlytics::recordException);

    remoteConfig
        .fetchAndActivate()
        .addOnFailureListener(crashlytics::recordException);

    // Atualiza as URLs do app
    remoteConfig.setDefaultsAsync(R.xml.urls_map)
                .addOnSuccessListener(t -> {
                  Map<String, String> urls = new Gson().fromJson(remoteConfig.getString("urls"),
                                                                 new TypeToken<Map<String,
                                                                     String>>() {
                                                                 }.getType());

                  // Se o usuário estiver logado, tenta atualizar a URL do campus
                  if (UserUtils.getCampus() != null) {
                    UserUtils.setURL(urls.get(UserUtils.getCampus()));
                  }
                })
                .addOnFailureListener(crashlytics::recordException);

    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll()
                                                                    .build());

    AppCompatDelegate.setDefaultNightMode(PreferenceManager.getDefaultSharedPreferences(
                                                               getBaseContext())
                                                           .getBoolean(NIGHT, false) ?
                                          AppCompatDelegate.MODE_NIGHT_YES
                                                                                     :
                                          AppCompatDelegate.MODE_NIGHT_NO);
  }

  public static Context getContext() {
    return context;
  }

}
