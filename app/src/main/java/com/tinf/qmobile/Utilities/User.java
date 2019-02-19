package com.tinf.qmobile.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private final static String TAG = "User";
    public static final String REGISTRATION = ".Reg";
    public static final String PASSWORD = ".Pass";
    public static final String INFO = ".Info";
    private static final String VALID = ".Valid";
    private static final String NAME = ".Name";
    private static final String LAST = ".Last";
    private static final String YEARS = ".Years";

    public static boolean isValid() {
        return getInfo().getBoolean(VALID, false);
    }

    public static void setValid(boolean isValid) {
        getEditor().putBoolean(VALID, isValid).apply();
    }

    public static String getName() {
        return getInfo().getString(NAME, "");
    }

    public static void setName(String name) {
        getEditor().putString(NAME, name).apply();
    }

    public static String getCredential(String TAG) {
        return getInfo().getString(TAG, "");
    }

    public static void setCredential(String TAG, String cred) {
        getEditor().putString(TAG, cred).apply();
    }

    public static long getLastLogin() {
        return getInfo().getLong(LAST, new Date().getTime());
    }

    public static void setLastLogin(Long date) {
        getEditor().putLong(LAST, date).apply();
    }

    public static void clearInfos() {
        getEditor().clear().apply();
    }

    public static SharedPreferences getInfo() {
        return App.getContext().getSharedPreferences(INFO, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor() {
        return getInfo().edit();
    }

    public static void setYears(String[] years) {
        JSONArray array = new JSONArray();

        for (String z : years) {
            array.put((String) z);
        }

        getEditor().putString(YEARS, array.toString()).apply();
    }

    public static String[] getYears() {
        List<String> years = new ArrayList<>();

        String string = getInfo().getString(YEARS, "");

        if (!TextUtils.isEmpty(string)) {
            try {
                JSONArray array = new JSONArray(string);

                if (array.length() > 0) {

                    for (int i = 0; i < array.length(); i++) {
                        years.add(array.getString(i));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return years.toArray(new String[0]);
    }

    public static int getYear(int i) {
        return Integer.valueOf(getYears()[i].substring(0, 4));
    }

    public static int getPeriod(int i) {
        return Integer.valueOf(getYears()[i].substring(7));
    }

    private static String encrypt(String value, String KEY_A, String KEY_B) {
        String encrypted = "", javaScriptCode = "";

        InputStream inputStream = App.getContext().getResources().openRawResource(R.raw.encrypt);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            javaScriptCode = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Object[] params = new Object[] { KEY_A, KEY_B, value };

        org.mozilla.javascript.Context rhino = org.mozilla.javascript.Context.enter();

        rhino.setOptimizationLevel(-1);
        try {
            Scriptable scope = rhino.initStandardObjects();

            rhino.evaluateString(scope, javaScriptCode, "encrypt", 1, null);

            Object obj = scope.get("encrypt", scope);

            if (obj instanceof Function) {
                Function jsFunction = (Function) obj;

                Object jsResult = jsFunction.call(rhino, scope, scope, params);
                encrypted = org.mozilla.javascript.Context.toString(jsResult);
            }

        } finally {
            org.mozilla.javascript.Context.exit();
        }
        return encrypted;
    }

    public static Map<String, String> getLoginParams(String KEY_A, String KEY_B) {
        Map<String, String>  params = new HashMap<>();
        params.put("LOGIN", encrypt(User.getCredential(REGISTRATION), KEY_A, KEY_B));
        params.put("SENHA", encrypt(User.getCredential(PASSWORD), KEY_A, KEY_B));
        params.put("Submit", encrypt("OK", KEY_A, KEY_B));
        params.put("TIPO_USU", encrypt("1", KEY_A, KEY_B));
        return params;
    }

}