package com.tinf.qmobile.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.tinf.qmobile.App;
import com.tinf.qmobile.R;
import com.tinf.qmobile.fragment.dialog.UserFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.tinf.qmobile.App.getContext;

public class User {

    public static final String REGISTRATION = ".Reg";
    public static final String PASSWORD = ".Pass";
    private static final String INFO = ".Info";
    private static final String VALID = ".Valid";
    private static final String NAME = ".Name";
    private static final String LAST = ".Last";
    private static final String YEARS = ".Years";
    private static final String URL = ".Url";

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
       return getInfo().getString(URL, "");
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

    private static SharedPreferences getInfo() {
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
                        years.add(array.getString(i).replaceAll("\\s+", ""));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (years.isEmpty()) {
            years.add("");
        }

        return years.toArray(new String[0]);
    }

    public static int getYear(int i) {
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

        if (getYears().length > 0) {
            crashlytics.setCustomKey("Register", User.getCredential(REGISTRATION));
            crashlytics.setCustomKey("URL", User.getURL());
            crashlytics.setCustomKey("Years", Arrays.toString(getYears()));

            if (getYears()[i].contains("/")) {
                String s = getYears()[i];
                return Integer.parseInt(s.substring(0, s.indexOf("/")));

            } else if (!getYears()[i].isEmpty()) {
                return Integer.parseInt(getYears()[i]);

            } else return 0;

        } else return 0;
    }

    public static int getPeriod(int i) {
        if (getYears().length > 0) {
            if (getYears()[i].contains("/")) {
                String s = getYears()[i];
                return Integer.parseInt(s.substring(s.indexOf("/") + 1));

            } else return 0;

        } else return 0;
    }

    public static Drawable getProfilePicture(Context context) {
        File picture = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + "/" + User.getCredential(User.REGISTRATION));

        if (picture.exists()) {
            Log.d("PICTURE", picture.getAbsolutePath());

            Bitmap bitmap = null;

            if (android.os.Build.VERSION.SDK_INT >= 29) {
                try {
                    ImageDecoder.Source src = ImageDecoder.createSource(picture);

                    bitmap = ImageDecoder.decodeBitmap(src);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                bitmap = BitmapFactory.decodeFile(picture.getAbsolutePath());
            }

            if (bitmap != null) {
                int size = Math.min(bitmap.getWidth(), bitmap.getHeight());

                RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(context.getResources(),
                        Bitmap.createBitmap(bitmap, 0,0, size, size));
                round.setCircular(true);
                round.setAntiAlias(true);

                return round.getCurrent();
            }
        }

        return null;
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