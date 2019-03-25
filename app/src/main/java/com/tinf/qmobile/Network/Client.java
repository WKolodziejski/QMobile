package com.tinf.qmobile.Network;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tinf.qmobile.Class.Materiais.Material;
import com.tinf.qmobile.Parsers.ReportParser;
import com.tinf.qmobile.Parsers.CalendarioParser;
import com.tinf.qmobile.Parsers.JournalParser;
import com.tinf.qmobile.Parsers.HorarioParser;
import com.tinf.qmobile.Parsers.MateriaisParser;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.RequestHelper;
import com.tinf.qmobile.Utilities.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;
import static com.tinf.qmobile.Activity.Settings.SettingsActivity.NOTIFY;
import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.Network.OnResponse.INDEX;
import static com.tinf.qmobile.Network.OnResponse.PG_ACESSO_NEGADO;
import static com.tinf.qmobile.Network.OnResponse.PG_BOLETIM;
import static com.tinf.qmobile.Network.OnResponse.PG_CALENDARIO;
import static com.tinf.qmobile.Network.OnResponse.PG_DIARIOS;
import static com.tinf.qmobile.Network.OnResponse.PG_GERADOR;
import static com.tinf.qmobile.Network.OnResponse.PG_HORARIO;
import static com.tinf.qmobile.Network.OnResponse.PG_LOGIN;
import static com.tinf.qmobile.Network.OnResponse.PG_MATERIAIS;
import static com.tinf.qmobile.Network.OnResponse.URL;

public class Client {
    private final static String TAG = "Network Client";
    private final static String GERADOR = "/qacademico/lib/rsa/gerador_chaves_rsa.asp";
    private final static String VALIDA = "/qacademico/lib/validalogin.asp";
    private List<RequestHelper> queue;
    private static Client singleton;
    private List<OnResponse> listeners;
    private RequestQueue requests;
    private OnEvent onEvent;
    private String COOKIE;
    private String KEY_A;
    private String KEY_B;
    public static int pos = 0;
    private boolean isValid;
    private boolean isLogging;

    public enum Resp {
        OK, HOST, DENIED, EGRESS
    }

    private Client() {
        requests = Volley.newRequestQueue(getContext(), new HurlStack());
        queue = new ArrayList<>();
        Log.v(TAG, "New instace created");
    }

    public static synchronized Client get() {
        if (singleton == null) {
            singleton = new Client();
        }
        return singleton;
    }

    private void createRequest(int pg, String url, int pos, int method, Map<String, String> form, boolean notify) {
        if (!isValid) {
            if (!isLogging) {
                login();
            }
            addToQueue(pg, url, pos, method, form, notify);
        } else {
            Log.i(TAG, "Request for: " + pg);
            addRequest(new StringRequest(method, URL + url,
                    response -> {
                        Resp r = testResponse(response);

                        if (r == Resp.DENIED) {
                            addToQueue(pg, url, pos, method, form, notify);
                            login();
                            callOnAccessDenied(pg, getContext().getResources().getString(R.string.login_expired));

                        } else if (r == Resp.OK) {
                            if (pg == PG_DIARIOS) {
                                new JournalParser(pos, notify, this::callOnFinish).execute(response);

                            } else if (pg == PG_BOLETIM) {
                                new ReportParser(pos, notify, this::callOnFinish).execute(response);

                            } else if (pg == PG_HORARIO) {
                                new HorarioParser(pos, notify, this::callOnFinish).execute(response);

                            } else if (pg == PG_MATERIAIS) {
                                new MateriaisParser(pos, notify, this::callOnFinish).execute(response);

                            } else if (pg == PG_CALENDARIO) {
                                new CalendarioParser(pos, notify, this::callOnFinish).execute(response);

                            }
                        }
                    }, error -> onError(pg, error.getMessage() == null ? getContext().getResources().getString(R.string.client_error) : error.getMessage())) {

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Cookie", COOKIE);
                    return params;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return !form.isEmpty() ? form : super.getParams();
                }
            }, pg, pos);
        }
    }

    public void load(int pg) {
        load(pg, pos);
    }

    public void load(int pg, int pos) {
        int method = GET;
        String url = INDEX + pg;
        Map<String, String> form = new HashMap<>();

        if (pos != 0) {
            switch (pg) {
                case PG_DIARIOS: method = POST;
                    form.put("ANO_PERIODO2", User.getYear(pos) + "_" + User.getPeriod(pos));
                    break;

                case PG_BOLETIM: method = GET;
                    url = url.concat("&cmbanos=" + User.getYear(pos) + "&cmbperiodos=" + User.getPeriod(pos));
                    break;

                case PG_HORARIO: method = GET;
                    url = url.concat("&cmbanos=" + User.getYear(pos) + "&cmbperiodos=" + User.getPeriod(pos));
                    break;

                case PG_MATERIAIS: method = POST;
                    form.put("ANO_PERIODO", User.getYear(pos) + "_" + User.getPeriod(pos));
                    break;
            }
        }

        createRequest(pg, url, pos, method, form, false);
    }

    public void checkChanges(int pg) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        createRequest(pg, INDEX + pg, 0, GET, new HashMap<>(), prefs.getBoolean(NOTIFY, true));
    }

    private <T> void addRequest(Request<T> request, int pg, int pos) {
        if (isConnected()) {
            callOnStart(pg, pos);
            if (requests == null) {
                requests = Volley.newRequestQueue(getContext(), new HurlStack());
            }
            requests.add(request);
            Log.v(TAG, "Loading: " + request);
        } else {
            onError(pg, getContext().getResources().getString(R.string.client_no_connection));
        }
    }

    public void login() {
        isLogging = true;
        fetchParams(sucess -> {
            addRequest(new StringRequest(POST, URL + VALIDA,
                    response -> {
                        Resp r = testResponse(response);

                        if (r == Resp.DENIED) {
                            callOnAccessDenied(PG_LOGIN, "");

                        } else if (r == Resp.OK) {
                            isValid = true;

                            Document document = Jsoup.parse(response);
                            String name = document.getElementsByClass("barraRodape").get(1).text();

                            User.setName(name);
                            User.setLastLogin(new Date().getTime());

                            isLogging = false;

                            callOnFinish(PG_LOGIN, 0);

                            String renewal = document.getElementsByClass("conteudoLink").get(2).text();

                            if (renewal.contains("matrícula")) {
                                callOnRenewalAvailable();
                            }
                        }
                    }, error -> onError(PG_LOGIN, error.getMessage() == null ? getContext().getResources().getString(R.string.client_error) : error.getMessage())) {

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

    private Resp testResponse(String response) {
        Document document = Jsoup.parse(response);

        Element strong = document.getElementsByTag("strong").first();

        if (strong != null) {
            String s = strong.text().trim();

            if (s.contains("Negado") || s.contains("negado")) {
                Element div = document.getElementsByClass("conteudoTexto").first();

                if (div != null) {
                    String msg = div.text().trim();

                    if (msg.contains("inativo")) {
                        User.clearInfos();
                        callOnAccessDenied(PG_ACESSO_NEGADO, msg);
                        return Resp.EGRESS;

                    }
                }

                return Resp.DENIED;
            }
        } else {
            Element p = document.getElementsByTag("p").first();

            if (p != null) {
                if (p.text().contains("inacessível")) {
                    callOnError(PG_LOGIN, getContext().getResources().getString(R.string.client_host));
                    return Resp.HOST;
                }
            }
        }
        return Resp.OK;
    }

    private void checkQueue() {
        if (queue != null && !queue.isEmpty()) {
            for (int i = 0; i < queue.size(); i++) {
                RequestHelper helper = queue.get(i);
                createRequest(helper.pg, helper.url, helper.pos, helper.method, helper.form, helper.notify);
                queue.remove(i);
            }
        }
    }

    private void addToQueue(int pg, String url, int pos, int method, Map<String, String> form, boolean notify) {
        if (queue == null) {
            queue = new ArrayList<>();
        }
        RequestHelper request = new RequestHelper(pg, url, pos, method, form, notify);
        if (!queue.contains(request)) {
            queue.add(request);
            Log.i(TAG, "Queued: " + pg + " for " + User.getYear(pos));
        }
    }

    private void fetchParams( Response.Listener<String> listener) {
        addRequest(new StringRequest(GET, URL + GERADOR,
                response ->  {
                    String keys = response.substring(response.indexOf("RSAKeyPair("), response.lastIndexOf(")"));;
                    keys = keys.substring(keys.indexOf("\"") + 1, keys.lastIndexOf("\""));

                    KEY_A = keys.substring(0, keys.indexOf("\""));
                    Log.d("Key A", KEY_A);
                    KEY_B = keys.substring(keys.lastIndexOf("\"") + 1);
                    Log.d("Key B", KEY_B);

                    Log.v(TAG, "Keys fetched");

                    listener.onResponse(response);

                }, error -> onError(PG_GERADOR, error.getMessage() == null ? getContext().getResources().getString(R.string.client_error) : error.getMessage())) {

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

    public void logOut() {
        Log.i(TAG, "Logout");
        queue.clear();
        requests.cancelAll(request -> true);
        KEY_A = "";
        KEY_B = "";
        COOKIE = "";
        pos = 0;
    }

    private void onError(int pg, String msg) {
        if (!isConnected()) {
            msg = getContext().getResources().getString(R.string.client_no_connection);
        } else {
            if (pg == PG_GERADOR) {
                msg = getContext().getResources().getString(R.string.client_host);
            } else if (pg == PG_LOGIN) {
                isLogging = false;
                isValid = false;
            }
        }

        Log.e(TAG, msg);

        callOnError(pg, msg);
    }

    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        }
        return false;
    }

    public void addOnResponseListener(OnResponse onResponse) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        if (onResponse != null && !listeners.contains(onResponse)) {
            this.listeners.add(onResponse);
            Log.i(TAG, "Added listener from " + onResponse);
        }
    }

    public void removeOnResponseListener(OnResponse onResponse) {
        if (listeners != null && onResponse != null) {
            listeners.remove(onResponse);
            Log.i(TAG, "Removed listener from " + onResponse);
        }
    }

    private void callOnError(int pg, String error) {
        Log.v(TAG, pg + ": " + error);
        requests.cancelAll(request -> true);
        isLogging = false;
        isValid = false;
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).onError(pg, error);
            }
        }
    }

    private void callOnStart(int pg, int pos) {
        Log.v(TAG, "Start: " + pg);
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).onStart(pg, pos);
            }
        }
    }

    private void callOnFinish(int pg, int pos) {
        Log.v(TAG, "Finish: " + pg);
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).onFinish(pg, pos);
            }
        }
        checkQueue();
    }

    private void callOnAccessDenied(int pg, String message) {
        Log.v(TAG, pg + ": " + message);
        isValid = false;
        isLogging = false;
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).onAccessDenied(pg, message);
            }
        }
    }

    public DownloadManager.Request download(Material material, String path, String name) {
        Uri uri = Uri.parse(URL + material.getLink());

        return new DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverRoaming(false)
                .addRequestHeader("Cookie", COOKIE)
                .setTitle(material.getTitle())
                .setDescription(material.getDateString())
                .setDestinationInExternalPublicDir("/Download" + path, name);
    }

    public void setOnEventListener(OnEvent onEvent) {
        this.onEvent = onEvent;
    }

    private void callOnRenewalAvailable() {
        if (onEvent != null) {
            onEvent.onRenewalAvailable();
        }
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean isLogging() {
        return isLogging;
    }

}
