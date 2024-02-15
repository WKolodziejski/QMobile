package com.tinf.qmobile.utility;

import static com.tinf.qmobile.utility.UserUtils.getEditor;
import static com.tinf.qmobile.utility.UserUtils.getInfo;

public final class EventsUtils {
  private static final String LENGTH = ".Events.Length";

  private EventsUtils() {}

  public static int getEventsLength() {
    return getInfo().getInt(LENGTH, 10);
  }

  public static void setEventsLength(int length) {
    getEditor().putInt(LENGTH, length).apply();
  }

}
