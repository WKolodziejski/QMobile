package com.tinf.qmobile.utility;

import static com.tinf.qmobile.utility.UserUtils.getEditor;
import static com.tinf.qmobile.utility.UserUtils.getInfo;

public class ScheduleUtils {
  private static final String START_HOUR = ".StartHour";
  private static final String END_HOUR = ".EndHour";
  private static final String START_MIN = ".StartMin";
  private static final String END_MIN = ".EndMin";
  private static final String AUTO = ".Auto";

  public static int getStartHour() {
    return getInfo().getInt(START_HOUR, 0);
  }

  public static int getEndHour() {
    return getInfo().getInt(END_HOUR, 0);
  }

  public static int getStartMin() {
    return getInfo().getInt(START_MIN, 0);
  }

  public static int getEndMin() {
    return getInfo().getInt(END_MIN, 0);
  }

  public static boolean isAuto() {
    return getInfo().getBoolean(AUTO, true);
  }

  public static void setStartHour(int hour) {
    getEditor().putInt(START_HOUR, hour).apply();
  }

  public static void setStartMin(int min) {
    getEditor().putInt(START_MIN, min).apply();
  }

  public static void setEndHour(int hour) {
    getEditor().putInt(END_HOUR, hour).apply();
  }

  public static void setEndMin(int min) {
    getEditor().putInt(END_MIN, min).apply();
  }

  public static void setAuto(boolean auto) {
    getEditor().putBoolean(AUTO, auto).apply();
  }

}
