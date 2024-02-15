package com.tinf.qmobile.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.color.ColorRoles;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.HarmonizedColors;
import com.google.android.material.color.HarmonizedColorsOptions;
import com.google.android.material.color.MaterialColors;

public final class ColorsUtils {
  private static Context harmonizedContext;
  private static Context dynamicContext;
  private static int nightMode;

  public static Context getHarmonizedContext(Context context) {
    if (ColorsUtils.harmonizedContext == null || nightModeChanged()) {
      ColorsUtils.harmonizedContext = HarmonizedColors.wrapContextIfAvailable(context,
                                                                              HarmonizedColorsOptions.createMaterialDefaults());
      nightMode = AppCompatDelegate.getDefaultNightMode();
    }

    return harmonizedContext;
  }

  public static Context getDynamicContext(Context context) {
    if (ColorsUtils.dynamicContext == null || nightModeChanged()) {
      ColorsUtils.dynamicContext = DynamicColors.wrapContextIfAvailable(context);
      nightMode = AppCompatDelegate.getDefaultNightMode();
    }

    return dynamicContext;
  }

  private static boolean nightModeChanged() {
    return nightMode != AppCompatDelegate.getDefaultNightMode();
  }

  public static ColorRoles harmonizeWithPrimary(Context context,
                                                int color) {
    return MaterialColors.getColorRoles(getHarmonizedContext(context),
                                        MaterialColors.harmonizeWithPrimary(
                                            getHarmonizedContext(context), color));
  }

  public static ColorRoles getColorRoles(Context context,
                                         int color) {
    return MaterialColors.getColorRoles(getDynamicContext(context),
                                        MaterialColors.getColor(getDynamicContext(context),
                                                                color,
                                                                Color.TRANSPARENT));
  }

  public static int getColor(Context context,
                             int color) {
    return MaterialColors.getColor(context, color,
                                   Color.TRANSPARENT);
  }

  public static void setSystemBarColor(Activity activity,
                                       int color) {
    activity.getWindow()
            .setStatusBarColor(MaterialColors.getColor(activity, color,
                                                       Color.TRANSPARENT));
  }

}
