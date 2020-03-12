package com.tinf.qmobile.utility;

import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static final String VERSION = ".v1.2.3";
    public static final String VERSION_INFO = ".Version";

    public static long getDate(String date, boolean isMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, isMonth ? 0 : 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.YEAR, getYear(date));
        cal.set(Calendar.MONTH, getMonth(date) - 1);
        cal.set(Calendar.DAY_OF_MONTH, getDay(date));
        return cal.getTimeInMillis();
    }

    public static long getDate(Date date, boolean isMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, isMonth ? 0 : 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private static int getDay(String date) {
        return Integer.parseInt(date.substring(0, date.indexOf("/")));
    }

    private static int getMonth(String date) {
        return Integer.parseInt(date.substring(date.indexOf("/") + 1, date.lastIndexOf("/")));
    }

    private static int getYear(String date) {
        return Integer.parseInt(date.substring(date.lastIndexOf("/") + 1, date.lastIndexOf("/") + 5));
    }

}
