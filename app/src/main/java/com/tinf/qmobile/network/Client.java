package com.tinf.qmobile.network;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tinf.qmobile.R;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.model.materiais.Material;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.parser.CalendarParser;
import com.tinf.qmobile.parser.ClassParser;
import com.tinf.qmobile.parser.JournalParser;
import com.tinf.qmobile.parser.MateriaisParser;
import com.tinf.qmobile.parser.ReportParser;
import com.tinf.qmobile.parser.ScheduleParser;
import com.tinf.qmobile.utility.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;
import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.activity.settings.SettingsActivity.NOTIFY;
import static com.tinf.qmobile.model.calendar.Utils.UPDATE_REQUEST;
import static com.tinf.qmobile.network.OnResponse.INDEX;
import static com.tinf.qmobile.network.OnResponse.PG_ACESSO_NEGADO;
import static com.tinf.qmobile.network.OnResponse.PG_BOLETIM;
import static com.tinf.qmobile.network.OnResponse.PG_CALENDARIO;
import static com.tinf.qmobile.network.OnResponse.PG_CLASSES;
import static com.tinf.qmobile.network.OnResponse.PG_DIARIOS;
import static com.tinf.qmobile.network.OnResponse.PG_FETCH_YEARS;
import static com.tinf.qmobile.network.OnResponse.PG_GERADOR;
import static com.tinf.qmobile.network.OnResponse.PG_HORARIO;
import static com.tinf.qmobile.network.OnResponse.PG_LOGIN;
import static com.tinf.qmobile.network.OnResponse.PG_MATERIAIS;

public class Client {
    private final static String TAG = "Network Client";
    private final static String GERADOR = "/qacademico/lib/rsa/gerador_chaves_rsa.asp";
    private final static String VALIDA = "/qacademico/lib/validalogin.asp";
    private List<RequestHelper> queue;
    private static Client singleton;
    private static String URL;
    private List<OnResponse> onResponses;
    private List<OnUpdate> onUpdates;
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
        try {
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{
                    new X509TrustManager(){
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}}},
                    new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
        requests = Volley.newRequestQueue(getContext(), new HurlStack());
        queue = new ArrayList<>();
        URL = User.getURL();
        Log.v(TAG, "New instace created");
    }

    public static synchronized Client get() {
        if (singleton == null) {
            singleton = new Client();
        }
        return singleton;
    }

    private void createRequest(int pg, String url, int pos, int method, Map<String, String> form, boolean notify, Matter matter) {
        if (!isValid) {
            if (!isLogging) {
                login();
            }
            addToQueue(pg, url, pos, method, form, notify, matter);
        } else {
            Log.i(TAG, "Request for: " + pg);
            addRequest(new StringRequest(method, URL + url,
                    response -> {
                        Resp r = testResponse(response);

                        if (r == Resp.DENIED) {
                            addToQueue(pg, url, pos, method, form, notify, matter);
                            login();
                            callOnAccessDenied(pg, getContext().getResources().getString(R.string.login_expired));

                        } else if (r == Resp.OK) {
                            if (pg == PG_DIARIOS) {
                                new JournalParser(pos, notify, this::callOnFinish, onEvent).execute(response);

                            } else if (pg == PG_BOLETIM) {
                                new ReportParser(pos, this::callOnFinish).execute(response);

                            } else if (pg == PG_HORARIO) {
                                new ScheduleParser(pos, this::callOnFinish).execute(response);

                            } else if (pg == PG_MATERIAIS) {
                                new MateriaisParser(pos, notify, this::callOnFinish).execute(response);

                            } else if (pg == PG_CALENDARIO) {
                                new CalendarParser(this::callOnFinish).execute(response);

                            } else if (pg == PG_CLASSES) {
                                new ClassParser(matter, pg, pos, notify, this::callOnFinish, this::callOnError).execute(response);

                            } else if (pg == PG_FETCH_YEARS) {
                                Document document = Jsoup.parse(response);

                                Elements dates = document.getElementsByTag("option");

                                String[] years = new String[dates.size() - 1];

                                for (int i = 0; i < dates.size() - 1; i++) {
                                    years[i] = dates.get(i + 1).text();
                                }

                                User.setYears(years);

                                callOnFinish(PG_FETCH_YEARS, 0);
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

    public void load(Matter matter) {
        createRequest(PG_CLASSES,
                INDEX + PG_DIARIOS + "&ACAO=VER_FREQUENCIA&COD_PAUTA=" + matter.getQID() + "&ANO_PERIODO=" + matter.getYear_() + "_" + matter.getPeriod_(),
                pos, POST, new HashMap<>(), false, matter);
    }

    public void load(int pg, int pos) {
        int method = GET;
        String url = INDEX + pg;
        Map<String, String> form = new HashMap<>();

        if (pg == PG_FETCH_YEARS) {
            url = INDEX + PG_DIARIOS;
        } else {
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

        createRequest(pg, url, pos, method, form, false, null);
    }

    public void checkChanges(int pg) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        createRequest(pg, INDEX + pg, 0, GET, new HashMap<>(), prefs.getBoolean(NOTIFY, true), null);
    }

    private <T> void addRequest(Request<T> request, int pg, int pos) {
        if (isConnected()) {
            callOnStart(pg, pos);
            if (requests == null) {
                requests = Volley.newRequestQueue(getContext(), new HurlStack());
            }
            request.setRetryPolicy(new DefaultRetryPolicy(0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

                            //String cod = document.getElementsByTag("q_latente").get(4).val();
                            //cod = cod.substring(cod.indexOf("=") + 1);

                            //downloadImage(cod);

                            String renewal = document.getElementsByClass("conteudoLink").get(2).text();

                            if (renewal.contains("matrícula")) {
                                callOnRenewalAvailable();
                            }

                            callOnFinish(PG_LOGIN, 0);

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

            if (s.contains("negado") || s.contains("Negado")) {
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
                createRequest(helper.pg, helper.url, helper.pos, helper.method, helper.form, helper.notify, helper.matter);
                queue.remove(i);
            }
        }
    }

    private void addToQueue(int pg, String url, int pos, int method, Map<String, String> form, boolean notify, Matter matter) {
        if (queue == null) {
            queue = new ArrayList<>();
        }
        RequestHelper request = new RequestHelper(pg, url, pos, method, form, notify, matter);
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
                        Log.d("Cookie 1", cookie1);
                        cookie1 = cookie1.substring(0, cookie1.indexOf(";")).concat("; ");
                        COOKIE = cookie1;

                        try {
                            String cookie2 = response.allHeaders.get(6).getValue();
                            cookie2 = cookie2.substring(0, cookie2.indexOf(";")).concat(";");
                            COOKIE = cookie1.concat(cookie2);
                        } catch (StringIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }

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

    public void clearRequests() {
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
                //msg = getContext().getResources().getString(R.string.client_host);
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
        if (onResponses == null) {
            onResponses = new ArrayList<>();
        }

        if (onResponse != null && !onResponses.contains(onResponse)) {
            this.onResponses.add(onResponse);
            Log.i(TAG, "Added listener from " + onResponse);
        }
    }

    public void removeOnResponseListener(OnResponse onResponse) {
        if (onResponses != null && onResponse != null) {
            onResponses.remove(onResponse);
            Log.i(TAG, "Removed listener from " + onResponse);
        }
    }

    public void addOnUpdateListener(OnUpdate onUpdate) {
        if (onUpdates == null) {
            onUpdates = new ArrayList<>();
        }

        if (onUpdate != null && !onUpdates.contains(onUpdate)) {
            this.onUpdates.add(onUpdate);
            Log.i(TAG, "Added listener from " + onUpdate);
        }
    }

    public void removeOnUpdateListener(OnUpdate onUpdate) {
        if (onUpdates != null && onUpdate != null) {
            onUpdates.remove(onUpdate);
            Log.i(TAG, "Removed listener from " + onUpdate);
        }
    }

    private void callOnError(int pg, String error) {
        requests.cancelAll(request -> true);
        isLogging = false;
        isValid = false;
        if (onResponses != null) {
            for (int i = 0; i < onResponses.size(); i++) {
                onResponses.get(i).onError(pg, error);
            }
        }
    }

    private void callOnStart(int pg, int pos) {
        Log.v(TAG, "Start: " + pg);
        if (onResponses != null) {
            for (int i = 0; i < onResponses.size(); i++) {
                onResponses.get(i).onStart(pg, pos);
            }
        }
    }

    private void callOnFinish(int pg, int pos) {
        Log.v(TAG, "Finish: " + pg);
        if (onResponses != null) {
            for (int i = 0; i < onResponses.size(); i++) {
                onResponses.get(i).onFinish(pg, pos);
            }
        }
        callOnUpdate(pg);
        checkQueue();
    }

    private void callOnAccessDenied(int pg, String message) {
        Log.v(TAG, pg + ": " + message);
        isValid = false;
        isLogging = false;
        clearRequests();
        if (onResponses != null) {
            for (int i = 0; i < onResponses.size(); i++) {
                onResponses.get(i).onAccessDenied(pg, message);
            }
        }
    }

    private void callOnDialog(String title, String message) {
        Log.v(TAG, message);
        if (onEvent != null) {
           onEvent.onDialog(title, message);
        }
    }

    private void callOnUpdate(int pg) {
        Log.v(TAG, "Update: " + pg);

        if (onUpdates != null) {
            for (int i = 0; i < onUpdates.size(); i++) {
                onUpdates.get(i).onUpdate(pg);
            }
        }
    }

    private void callOnScrollRequest() {
        if (onUpdates != null) {
            for (int i = 0; i < onUpdates.size(); i++) {
                onUpdates.get(i).onScrollRequest();
            }
        }
    }

    public void requestUpdate() {
        new Handler().postDelayed(() -> {
            callOnUpdate(UPDATE_REQUEST);
        }, 100);
    }

    public void requestScroll() {
        callOnScrollRequest();
    }

    public DownloadManager.Request downloadMaterial(Material material, String path, String name) {
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

    private void downloadImage(String cod) {
        File picture = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + User.getCredential(User.REGISTRATION));

        if (!picture.exists()) {

            Log.i(TAG, "Downloading profile picture");

            DownloadManager manager = (DownloadManager) getContext().getSystemService(DOWNLOAD_SERVICE);

            Uri uri = Uri.parse(URL + INDEX + "1025&tipo=0&COD=" + cod);

            manager.enqueue(new DownloadManager.Request(uri)
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                    .setAllowedOverRoaming(false)
                    .addRequestHeader("Cookie", COOKIE)
                    .setDestinationInExternalFilesDir(getContext(), Environment.DIRECTORY_PICTURES, User.getCredential(User.REGISTRATION)));
        }
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

    public void setURL(String url) {
        URL = url;
        User.setURL(url);
    }

}
