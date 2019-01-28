package com.tinf.qmobile.Network;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Interfaces.Network.OnResponse;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;
import static com.android.volley.Request.Method.POST;
import static com.tinf.qmobile.Utilities.User.INFO;
import static com.tinf.qmobile.Utilities.User.PASSWORD;
import static com.tinf.qmobile.Utilities.User.REGISTRATION;
import static com.tinf.qmobile.Utilities.Utils.YEARS;

public class NetworkSingleton {
    private static final String TAG = "NetworkSingleton";
    private static final String GERADOR = "http://qacademico.ifsul.edu.br//qacademico/lib/rsa/gerador_chaves_rsa.asp";
    private static final String VALIDA = "http://qacademico.ifsul.edu.br/qacademico/lib/validalogin.asp";
    private static NetworkSingleton singleton;
    private OnResponse onResponse;
    private RequestQueue requestQueue;
    private String COOKIE;
    private String KEY_A;
    private String KEY_B;

    private NetworkSingleton(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        fetchParams(context);
        Log.v(TAG, "New instace created");
    }

    public static synchronized NetworkSingleton getInstance(Context context) {
        if (singleton == null) {
            singleton = new NetworkSingleton(context);
        }
        return singleton;
    }

    public void createRequest(String url, Response.Listener<String> listener,
                                       @Nullable Response.ErrorListener errorListener) {
        addRequest(new StringRequest(url, listener, errorListener) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Cookie", COOKIE);
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        });
    }

    private <T> void addRequest(Request<T> request) {
        requestQueue.add(request);
        Log.v(TAG, "Added to queue: " + request.toString());

    }

    private void fetchParams(Context context) {
        requestQueue.add(new StringRequest(Request.Method.GET, GERADOR,
                response ->  {
                    String keys = response.substring(response.indexOf("RSAKeyPair("), response.lastIndexOf(")"));;
                    keys = keys.substring(keys.indexOf("\"") + 1, keys.lastIndexOf("\""));

                    KEY_A = keys.substring(0, keys.indexOf("\""));
                    Log.d("Key A", KEY_A);
                    KEY_B = keys.substring(keys.lastIndexOf("\"") + 1);
                    Log.d("Key B", KEY_B);

                    Log.v(TAG, "Keys fetched");

                    addRequest(loginRequest(context));

                }, error -> {
                    Log.e(TAG, error.getCause().getMessage());
                    onResponse.OnError("", error);
                }) {

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        String cookie1 = response.allHeaders.get(5).getValue();
                        cookie1 = cookie1.substring(0, cookie1.indexOf(";")).concat("; ");

                        String cookie2 = response.allHeaders.get(6).getValue();
                        cookie2 = cookie2.substring(0, cookie2.indexOf(";")).concat(";");

                        COOKIE = cookie1.concat(cookie2);
                        Log.d("Cookie", COOKIE);

                        Log.v(TAG, "Cookie fetched");

                        return super.parseNetworkResponse(response);
                    }
        });
    }

    private StringRequest loginRequest(Context context) {
        return new StringRequest(POST, VALIDA,
                response -> {
                    Document homePage = Jsoup.parse(response);

                    if(homePage.getElementsByTag("strong").first().text().contains("Negado")) {
                        //TODO
                    } else {
                        String name = homePage.getElementsByClass("barraRodape").get(1).text();
                        User.setName(context, name);
                        User.setLastLogin(context, new Date().getTime());
                    }

                    onResponse.OnFinish("", response);

                }, error -> {

                    onResponse.OnError("", error);
                    Log.d(TAG, error.getMessage());

                }) {

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String,String> params = new HashMap<>();
                        params.put("Cookie", COOKIE);
                        return params;
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        return getLoginParams(context);
                    }
        };
    }

    private Map<String, String> getLoginParams(Context context) {
        Map<String, String>  params = new HashMap<>();
        params.put("LOGIN", encrypt(context, User.getCredential(context, REGISTRATION)));
        params.put("SENHA", encrypt(context, User.getCredential(context, PASSWORD)));
        //params.put("LOGIN", encrypt(context, "20151INF_i0128"));
        //params.put("SENHA", encrypt(context, "Luxemburgo"));
        params.put("Submit", encrypt(context, "OK"));
        params.put("TIPO_USU", encrypt(context, "1"));
        return params;
    }

    private String encrypt(Context context, String value) {
        String encrypted = "", javaScriptCode = "";

        InputStream inputStream = context.getResources().openRawResource(R.raw.encrypt);
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

    public static void setYears(Context context, String[] years) {
        JSONArray array = new JSONArray();

        for (String z : years) {
            array.put((String) z);
        }

        User.getEditor(context).putString(YEARS, array.toString()).apply();
    }

    public static String[] getYears(Context context) {
        List<String> years = new ArrayList<>();
        String string = User.getInfo(context).getString(YEARS, "");
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

    public void setOnResponseListener(OnResponse onResponse) {
        this.onResponse = onResponse;
    }

}
