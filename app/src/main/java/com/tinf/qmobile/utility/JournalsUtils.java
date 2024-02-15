package com.tinf.qmobile.utility;

import static com.tinf.qmobile.utility.UserUtils.getEditor;
import static com.tinf.qmobile.utility.UserUtils.getInfo;

public final class JournalsUtils {
  private static final String ORDER = ".Journals.Order";

  private JournalsUtils() {}

  public static String getOrder() {
    return getInfo().getString(ORDER, "ASC");
  }

  public static void setOrder(String order) {
    getEditor().putString(ORDER, order).apply();
  }
}
