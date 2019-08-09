package com.tinf.qmobile.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.tinf.qmobile.network.OnResponse.IFSUL;

public class User {

    public enum Type {
        SEMESTRE1(0), BIMESTRE(1), UNICO(2), SEMESTRE2(3);

        private int i;

        Type(final int i) {
            this.i = i;
        }

        public int get() {
            return i;
        }
    }

    private final static String TAG = "User";
    public static final String REGISTRATION = ".Reg";
    public static final String PASSWORD = ".Pass";
    private static final String INFO = ".Info";
    private static final String VALID = ".Valid";
    private static final String NAME = ".Name";
    private static final String LAST = ".Last";
    private static final String YEARS = ".Years";
    private static final String URL = ".Url";
    private static final String TYPE = ".Type";
    private static final String NIGHT = ".Night";

    public static boolean isNight() {
        return getInfo().getBoolean(NIGHT, false);
    }

    public static void setNight(boolean isValid) {
        getEditor().putBoolean(NIGHT, isValid).apply();
    }

    public static boolean isValid() {
        return getInfo().getBoolean(VALID, false);
    }

    public static void setValid(boolean isValid) {
        getEditor().putBoolean(VALID, isValid).apply();
    }

    public static void setURL(String url) {
        getEditor().putString(URL, url).apply();
    }

    public static String getURL() {
       return getInfo().getString(URL, IFSUL);
    }

    public static void setType(Type type) {
        getEditor().putInt(TYPE, type.get()).apply();
    }

    public static int getType() {
        return getInfo().getInt(TYPE, 0);
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

    public static String getLastLogin() {
        Date date = new Date(getInfo().getLong(LAST, new Date().getTime()));
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return format.format(date);
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