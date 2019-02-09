package com.tinf.qmobile.Network;

import android.content.SharedPreferences;
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
import com.tinf.qmobile.Utilities.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import androidx.annotation.Nullable;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;

public class Client {
    private static final String TAG = "NetworkSingleton";
    public static final String URL = "http://qacademico.ifsul.edu.br//qacademico/index.asp?t=";
    public static final String GERADOR = "http://qacademico.ifsul.edu.br//qacademico/lib/rsa/gerador_chaves_rsa.asp";
    public static final String VALIDA = "http://qacademico.ifsul.edu.br/qacademico/lib/validalogin.asp";
    public static final int PG_LOGIN = 1001;
    public static final int PG_HOME = 2000;
    public static final int PG_DIARIOS = 2071;
    public static final int PG_BOLETIM = 2032;
    public static final int PG_HORARIO = 2010;
    public static final int PG_MATERIAIS = 2061;
    public static final int PG_CALENDARIO = 2020;
    public static final int PG_ERRO = 1;
    public static final int PG_ACESSO_NEGADO = 3;
    private static Client singleton;
    private OnResponse onResponse;
    private RequestQueue requestQueue;
    private String COOKIE;
    private String KEY_A;
    private String KEY_B;
    public int year = 0;
    private boolean isValid;

    private Client() {
        requestQueue = Volley.newRequestQueue(App.getContext());
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
                        isValid = false;
                        //TODO colocar este request na fila e fazer login
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
                }, error ->  onResponse.onError(pg, error.getMessage() == null ? "Erro desconhecido" : error.getMessage())) {

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
        }, year);
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

    private <T> void addRequest(Request<T> request, int year) {
        onResponse.onStart(request.getUrl(), year);
        requestQueue.add(request);
        Log.v(TAG, "Added to queue: " + request.toString());
    }

    public void login() {
        fetchParams(response -> {
            addRequest(loginRequest(), 0);
        }, error -> {
            //TODO servidor indisponível ou sem conexão
            onResponse.onError(PG_LOGIN, "SERVIDOR INDISPONÍVEL");
        });
    }

    private void fetchParams( Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        addRequest(new StringRequest(GET, GERADOR,
                response ->  {
                    String keys = response.substring(response.indexOf("RSAKeyPair("), response.lastIndexOf(")"));;
                    keys = keys.substring(keys.indexOf("\"") + 1, keys.lastIndexOf("\""));

                    KEY_A = keys.substring(0, keys.indexOf("\""));
                    Log.d("Key A", KEY_A);
                    KEY_B = keys.substring(keys.lastIndexOf("\"") + 1);
                    Log.d("Key B", KEY_B);

                    Log.v(TAG, "Keys fetched");

                    listener.onResponse("");

                }, errorListener::onErrorResponse) {

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

        }, 0);
    }

    private StringRequest loginRequest() {
        return new StringRequest(POST, VALIDA,
                response -> {
                    if (wasDenied(response)) {
                        isValid = false;

                        //TODO adiconar mensagem de acesso negado
                        onResponse.onAccessDenied(PG_LOGIN, "");
                    } else {
                        isValid = true;

                        Document page = Jsoup.parse(response);
                        String name = page.getElementsByClass("barraRodape").get(1).text();
                        User.setName(name);
                        User.setLastLogin(new Date().getTime());

                        //TODO verificar se existe outro request pendente senão chamar o listener abaixo
                                                                    onResponse.onFinish(PG_LOGIN, 0);
                    }
                }, error ->  onResponse.onError(PG_LOGIN, error.getMessage() == null ? "Erro desconhecido" : error.getMessage())) {

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
        };
    }

    private boolean wasDenied(String response) {
        Document page = Jsoup.parse(response);
        String msg = page.getElementsByTag("strong").first().text().trim();
        return msg.contains("Negado");
    }

    public void setOnResponseListener(OnResponse onResponse) {
        this.onResponse = onResponse;
    }

}
