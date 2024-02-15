package com.tinf.qmobile;

import static com.tinf.qmobile.fragment.SettingsFragment.NIGHT;
import static com.tinf.qmobile.utility.UserUtils.PASSWORD;
import static com.tinf.qmobile.utility.UserUtils.REGISTRATION;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.google.android.material.color.DynamicColors;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.tinf.qmobile.utility.UserUtils;

import io.objectbox.BoxStore;

public class App extends Application {
  public static final String VERSION = ".v1.8.8";
  public static final String VERSION_INFO = ".Version";
  public static final String USE_INFO = ".use";
  public static final String USE_COUNT = ".count";
  public static final String USE_RATED = ".rated";
  private static Context context;

  @Override
  public void onCreate() {
    super.onCreate();
    context = getBaseContext();

    FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
    crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);
    crashlytics.setCustomKey("Register", UserUtils.getCredential(REGISTRATION));
    crashlytics.setCustomKey("Password", UserUtils.getCredential(PASSWORD));
    crashlytics.setCustomKey("URL", UserUtils.getURL());

    FirebaseRemoteConfig
        .getInstance()
        .setConfigSettingsAsync(new FirebaseRemoteConfigSettings
            .Builder()
                                    .setMinimumFetchIntervalInSeconds(43200) // 12h
                                    .build())
        .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));

    FirebaseRemoteConfig
        .getInstance()
        .fetchAndActivate()
        .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));

    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

    AppCompatDelegate.setDefaultNightMode(PreferenceManager.getDefaultSharedPreferences(
        getBaseContext()).getBoolean(NIGHT, false) ?
                                          AppCompatDelegate.MODE_NIGHT_YES
                                                   : AppCompatDelegate.MODE_NIGHT_NO);

    if (getSharedPreferences(VERSION_INFO, MODE_PRIVATE).getBoolean(VERSION, true) &&
        BoxStore.deleteAllFiles(getBaseContext(), UserUtils.getCredential(REGISTRATION))) {
      getSharedPreferences(VERSION_INFO, MODE_PRIVATE).edit().putBoolean(VERSION, false).apply();
    }
  }

  public static Context getContext() {
    return context;
  }

}
