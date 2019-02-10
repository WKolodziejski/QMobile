package com.tinf.qmobile.Network;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Interfaces.OnResponse;
import com.tinf.qmobile.Parsers.BoletimParser;
import com.tinf.qmobile.Parsers.CalendarioParser;
import com.tinf.qmobile.Parsers.DiariosParser;
import com.tinf.qmobile.Parsers.HorarioParser;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.RequestHelper;
import com.tinf.qmobile.Utilities.User;
import com.tinf.qmobile.Utilities.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;

public class Client {
    private static final String TAG = "NetworkSingleton";
    public static final String URL = "http://qacademico.ifsul.edu.br//qacademico/index.asp?t=";
    private static final String GERADOR = "http://qacademico.ifsul.edu.br//qacademico/lib/rsa/gerador_chaves_rsa.asp";
    private static final String VALIDA = "http://qacademico.ifsul.edu.br/qacademico/lib/validalogin.asp";
    public static final int PG_LOGIN = 1001;
    public static final int PG_HOME = 2000;
    public static final int PG_DIARIOS = 2071;
    public static final int PG_BOLETIM = 2032;
    public static final int PG_HORARIO = 2010;
    public static final int PG_MATERIAIS = 2061;
    public static final int PG_CALENDARIO = 2020;
    public static final int PG_ERRO = 1;
    public static final int PG_GERADOR = 2;
    public static final int PG_ACESSO_NEGADO = 3;
    private List<RequestHelper> queue;
    private static Client singleton;
    private OnResponse onResponse;
    private RequestQueue request;
    private String COOKIE;
    private String KEY_A;
    private String KEY_B;
    public int year = 0;

    private Client() {
        request = Volley.newRequestQueue(App.getContext());
        queue = new ArrayList<>();
        Log.v(TAG, "New instace created");
    }

    public static synchronized Client get() {
        if (singleton == null) {
            singleton = new Client();
        }
        return singleton;
    }

    private void createRequest(int pg, String url, int year, int method, Map<String, String> form, boolean notify) {
        addRequest(new StringRequest(method, URL + pg + url,
                response -> {
                    if (wasDenied(response)) {
                        queue.add(new RequestHelper(pg, url, year, method, form, notify));

                        //TODO add msg
                        onResponse.onAccessDenied(pg, "Seção Expirada");
                        login();
                    } else {
                        if (pg == PG_DIARIOS) {
                            new DiariosParser(year, notify, onResponse).execute(response);

                        } else if (pg == PG_BOLETIM) {
                            new BoletimParser(year, notify, onResponse).execute(response);

                        } else if (pg == PG_HORARIO) {
                            new HorarioParser(year, notify, onResponse).execute(response);

                        } else if (pg == PG_CALENDARIO) {
                            new CalendarioParser(notify, onResponse).execute(response);
                        }
                    }
                }, error ->  onError(pg, error.getMessage() == null ? "Erro desconhecido" : error.getMessage())) {

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String,String> params = new HashMap<>();
                        params.put("Cookie", COOKIE);
                        return params;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return !form.isEmpty() ? form : super.getParams();
                    }
        }, pg, year);
    }

    public void load(int pg) {
        createRequest(pg, "", 0, GET, new HashMap<>(), false);
    }

    public void load(int pg, int year) {
        int method = GET;
        String url = "";
        Map<String, String> form = new HashMap<>();

        if (year != 0) {
            switch (pg) {
                case PG_DIARIOS: method = POST;
                    form.put("ANO_PERIODO2", User.getYear(year) + "_1");
                    break;

                case PG_BOLETIM: method = GET;
                    url = "&cmbanos=" + User.getYears()[year];
                    break;

                case PG_HORARIO: method = GET;
                    url = "&cmbanos=" + User.getYears()[year];
                    break;

                case PG_MATERIAIS: method = POST;
                    form.put("ANO_PERIODO", User.getYear(year) + "_1");
                    break;
            }
        }

        createRequest(pg, url, year, method, form, false);
    }

    public void checkChanges(int pg) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        createRequest(pg, "", 0, GET, new HashMap<>(), prefs.getBoolean("key_notifications", true));
    }

    private <T> void addRequest(Request<T> request, int pg, int year) {
        if (isConnected()) {
            onResponse.onStart(pg, year);
            this.request.add(request);
            Log.v(TAG, "Added to queue: " + request.toString());
        } else {
            onError(pg, App.getContext().getResources().getString(R.string.text_no_connection));
        }
    }

    public void login() {
        fetchParams(sucess -> {
            addRequest(new StringRequest(POST, VALIDA,
                    response -> {
                        if (wasDenied(response)) {

                            //TODO adiconar mensagem de acesso negado
                            onResponse.onAccessDenied(PG_LOGIN, App.getContext().getResources().getString(R.string.text_invalid_login));
                        } else {
                            Document page = Jsoup.parse(response);
                            String name = page.getElementsByClass("barraRodape").get(1).text();
                            User.setName(name);
                            User.setLastLogin(new Date().getTime());

                            checkQueue();

                            onResponse.onFinish(PG_LOGIN, 0);
                        }
                    }, error -> onError(PG_LOGIN, error.getMessage() == null ? "Erro desconhecido" : error.getMessage())) {

                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String,String> params = new HashMap<>();
                            params.put("Cookie", COOKIE);
                            return params;
                        }

                        @Override
                        protected Map<String, String> getParams() {
                            return User.getLoginParams(KEY_A, KEY_B);
                        }

                        @Override
                        public Priority getPriority() {
                            return Priority.IMMEDIATE;
                        }

            }, PG_LOGIN, 0);
        });
    }

    private void checkQueue() {
        if (queue != null && !queue.isEmpty()) {
            for (int i = 0; i < queue.size(); i++) {
                RequestHelper helper = queue.get(i);
                createRequest(helper.pg, helper.url, helper.year, helper.method, helper.form, helper.notify);
                queue.remove(i);
            }
        }
    }

    private void fetchParams( Response.Listener<String> listener) {
        addRequest(new StringRequest(GET, GERADOR,
                response ->  {
                    String keys = response.substring(response.indexOf("RSAKeyPair("), response.lastIndexOf(")"));;
                    keys = keys.substring(keys.indexOf("\"") + 1, keys.lastIndexOf("\""));

                    KEY_A = keys.substring(0, keys.indexOf("\""));
                    Log.d("Key A", KEY_A);
                    KEY_B = keys.substring(keys.lastIndexOf("\"") + 1);
                    Log.d("Key B", KEY_B);

                    Log.v(TAG, "Keys fetched");

                    listener.onResponse(response);

                }, error -> onError(PG_GERADOR, error.getMessage() == null ? "Erro desconhecido" : error.getMessage())) {

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

                    @Override
                    public Priority getPriority() {
                        return Priority.IMMEDIATE;
                    }

        }, PG_GERADOR,0);
    }

    private void onError(int pg, String msg) {
        //TODO mensagens de erro
        if (!isConnected()) {
            msg = App.getContext().getResources().getString(R.string.text_no_connection);
        } else {
            if (pg == PG_GERADOR) {
                msg = "Servidor indisponível";
            }
        }

        Log.e(TAG, msg);

        onResponse.onError(pg, msg);
    }

    private boolean wasDenied(String response) {
        Document page = Jsoup.parse(response);
        String msg = page.getElementsByTag("strong").first().text().trim();
        return msg.contains("Negado");
    }

    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        }
        return false;
    }

    public void setOnResponseListener(OnResponse onResponse) {
        this.onResponse = onResponse;
    }

}
