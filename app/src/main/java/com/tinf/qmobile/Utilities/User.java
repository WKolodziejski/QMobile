package com.tinf.qmobile.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class User {
    public static final String REGISTRATION = ".Matricula";
    public static final String PASSWORD = ".Senha";
    public static final String INFO = ".Login_Info";
    private static final String VALID = ".Valido";
    private static final String NAME = ".Nome";
    private static final String LAST = ".Last_Login";

    public static boolean isValid(Context context) {
        return getInfo(context).getBoolean(VALID, false);
    }

    public static void setValid(Context context, boolean isValid) {
        getEditor(context).putBoolean(VALID, isValid).apply();
    }

    public static String getName(Context context) {
        return getInfo(context).getString(NAME, "");
    }

    public static void setName(Context context, String name) {
        getEditor(context).putString(NAME, name).apply();
    }

    public static String getCredential(Context context, String TAG) {
        return getInfo(context).getString(TAG, "");
    }

    public static void setCredential(Context context, String TAG, String cred) {
        getEditor(context).putString(TAG, cred).apply();
    }

    public static long getLastLogin(Context context) {
        return getInfo(context).getLong(LAST, new Date().getTime());
    }

    public static void setLastLogin(Context context, Long date) {
        getEditor(context).putLong(LAST, date).apply();
    }

    public static void clearInfos(Context context) {
        getEditor(context).clear().apply();
    }

    public static SharedPreferences getInfo(Context context) {
        return context.getSharedPreferences(INFO, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getInfo(context).edit();
    }

}

