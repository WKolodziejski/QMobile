package com.tinf.qmobile.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.webkit.CookieManager;

import androidx.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Header;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tinf.qmobile.R;
import com.tinf.qmobile.database.DataBase;
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.model.matter.Matter;
import com.tinf.qmobile.model.matter.Matter_;
import com.tinf.qmobile.parser.BaseParser;
import com.tinf.qmobile.parser.CalendarParser;
import com.tinf.qmobile.parser.ClassParser;
import com.tinf.qmobile.parser.JournalParser;
import com.tinf.qmobile.parser.MaterialsParser;
import com.tinf.qmobile.parser.MessageParser;
import com.tinf.qmobile.parser.ReportParser;
import com.tinf.qmobile.parser.ScheduleParser;
import com.tinf.qmobile.service.DownloadReceiver;
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

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;
import static com.tinf.qmobile.App.getContext;
import static com.tinf.qmobile.fragment.SettingsFragment.NOTIFY;
import static com.tinf.qmobile.network.OnResponse.INDEX;
import static com.tinf.qmobile.network.OnResponse.MESSAGES;
import static com.tinf.qmobile.network.OnResponse.PG_ACCESS_DENIED;
import static com.tinf.qmobile.network.OnResponse.PG_CALENDAR;
import static com.tinf.qmobile.network.OnResponse.PG_CLASSES;
import static com.tinf.qmobile.network.OnResponse.PG_FETCH_YEARS;
import static com.tinf.qmobile.network.OnResponse.PG_GENERATOR;
import static com.tinf.qmobile.network.OnResponse.PG_JOURNALS;
import static com.tinf.qmobile.network.OnResponse.PG_LOGIN;
import static com.tinf.qmobile.network.OnResponse.PG_MATERIALS;
import static com.tinf.qmobile.network.OnResponse.PG_MESSAGES;
import static com.tinf.qmobile.network.OnResponse.PG_REPORT;
import static com.tinf.qmobile.network.OnResponse.PG_SCHEDULE;

public class Client {
    private final static String TAG = "Network Client";
    private final static String GERADOR = "/qacademico/lib/rsa/gerador_chaves_rsa.asp";
    private final static String VALIDA = "/qacademico/lib/validalogin.asp";
    private static Client instance;
    private List<RequestHelper> queue;
    private String URL;
    private List<OnResponse> onResponses;
    private List<OnUpdate> onUpdates;
    private RequestQueue requests;
    //private String COOKIE;
    private Map<String, String> params;
    private String KEY_A;
    private String KEY_B;
    public static int pos = 0;
    private boolean isValid;
    private boolean isLogging;

    public enum Resp {
        OK, HOST, DENIED, EGRESS
    }

    private Client() {
        /*try {
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
        }*/
        requests = Volley.newRequestQueue(getContext(), new HurlStack());
        queue = new ArrayList<>();
        params = new HashMap<>();
        onUpdates = new ArrayList<>();
        onResponses = new ArrayList<>();
        URL = User.getURL();
    }

    public static synchronized Client get() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    private void createRequest(int pg, String url, int pos, int method, Map<String, String> form, boolean notify, Matter matter, BaseParser.OnFinish onFinish) {
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
                            if (pg == PG_JOURNALS) {
                                new JournalParser(pg, pos, notify, onFinish, this::callOnError).execute(response);

                            } else if (pg == PG_REPORT) {
                                new ReportParser(pg, pos, notify, onFinish, this::callOnError).execute(response);

                            } else if (pg == PG_SCHEDULE) {
                                new ScheduleParser(pg, pos, notify, onFinish, this::callOnError).execute(response);

                            } else if (pg == PG_MATERIALS) {
                                new MaterialsParser(pg, pos, notify, onFinish, this::callOnError).execute(response);

                            } else if (pg == PG_CALENDAR) {
                                new CalendarParser(pg, pos, notify, onFinish, this::callOnError).execute(response);

                            } else if (pg == PG_MESSAGES) {
                                new MessageParser(pg, pos, notify, onFinish, this::callOnError).execute(response);

                            } else if (pg == PG_CLASSES) {
                                new ClassParser(matter, pg, pos, notify, onFinish, this::callOnError).execute(response);

                            } else if (pg == PG_FETCH_YEARS) {
                                Document document = Jsoup.parse(response);

                                //Element frm = document.getElementById("frmConsultar");

                                Element frm = document.getElementById("ANO_PERIODO2");

                                if (frm != null) {
                                    Elements dates = frm.getElementsByTag("option");

                                    if (dates != null) {
                                        String[] years = new String[dates.size() - 1];

                                        for (int i = 0; i < dates.size() - 1; i++)
                                            years[i] = dates.get(i + 1).text();

                                        User.setYears(years);
                                    }
                                }

                                callOnFinish(PG_FETCH_YEARS, 0);

                                loadYear(0);
                            }
                        }
                    }, error -> onError(pg, error.getMessage() == null ? getContext().getResources().getString(R.string.client_error) : error.getMessage())) {

                @Override
                public Map<String, String> getHeaders() {
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
        load(pg, pos, this::callOnFinish);
    }

    public void load(long id) {
        Matter matter = DataBase.get().getBoxStore()
                .boxFor(Matter.class)
                .get(id);

        load(matter);
    }

    public void load(Matter matter) {
        Log.d("Client", "loading " + matter.getTitle());

        createRequest(PG_CLASSES,
                INDEX + PG_JOURNALS + "&ACAO=VER_FREQUENCIA&COD_PAUTA=" + matter.getQID() + "&ANO_PERIODO=" + matter.getYear_() + "_" + matter.getPeriod_(),
                pos, POST, new HashMap<>(), false, matter, this::callOnFinish);
    }

    private void load(int pg, int pos, BaseParser.OnFinish onFinish) {
        load(pg, pos, onFinish, false);
    }

    private void load(int pg, boolean notify, BaseParser.OnFinish onFinish) {
        load(pg, 0, onFinish, notify);
    }

    private void load(int pg, int pos, BaseParser.OnFinish onFinish, boolean notify) {
        int method = GET;
        String url = INDEX + pg;
        Map<String, String> form = new HashMap<>();

        if (pg == PG_FETCH_YEARS) {
            url = INDEX + PG_JOURNALS;
        } else if (pg == PG_MESSAGES) {
            url = MESSAGES;
        } else {
            switch (pg) {
                case PG_JOURNALS: method = POST;
                    form.put("ANO_PERIODO2", User.getYear(pos) + "_" + User.getPeriod(pos));
                    break;

                case PG_REPORT:

                case PG_SCHEDULE:
                    method = GET;
                    url = url.concat("&cmbanos=" + User.getYear(pos) + "&cmbperiodos=" + User.getPeriod(pos));
                    break;

                case PG_MATERIALS: method = POST;
                    form.put("ANO_PERIODO", User.getYear(pos) + "_" + User.getPeriod(pos));
                    break;
            }
        }

        createRequest(pg, url, pos, method, form, notify, null, onFinish);
    }

    private <T> void addRequest(Request<T> request, int pg, int pos) {
        if (isConnected()) {
            callOnStart(pg, pos);
            request.setRetryPolicy(new DefaultRetryPolicy());
            requests.add(request);
            Log.v(TAG, "Loading: " + request);
        } else {
            onError(pg, getContext().getResources().getString(R.string.client_no_connection));
        }
    }

    public void login() {
        isLogging = true;
        fetchParams(success -> addRequest(new StringRequest(POST, URL + VALIDA,
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

                        String cod = document.getElementsByTag("q_latente").get(4).val();
                        cod = cod.substring(cod.indexOf("=") + 1);

                        Element img = document.getElementsByAttributeValueEnding("src", cod).first();

                        if (img != null)
                            downloadImage(cod);

                        callOnFinish(PG_LOGIN, 0);
                    }
                }, error -> onError(PG_LOGIN, error.getMessage() == null ? getContext().getResources().getString(R.string.client_error) : error.getMessage())) {

                    @Override
                    public Map<String, String> getHeaders() {
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

        }, PG_LOGIN, 0));
    }

    private Resp testResponse(String response) {
        Document document = Jsoup.parse(response);

        Element strong = document.getElementsByTag("strong").first();

        if (strong != null) {
            String s = strong.text().trim();

            if (s.contains("negado") || s.contains("Negado")) {
                Element div = document.getElementsByClass("conteudoTexto").first();

                if (div != null) {
                    div.select("br").after("\\n");
                    String msg = div.text().replaceAll("\\\\n", "\n").trim();

                    if (msg.contains("inativo")) {
                        User.clearInfos();
                        callOnAccessDenied(PG_ACCESS_DENIED, msg);
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
        while (!queue.isEmpty()) {
            RequestHelper helper = queue.get(0);
            createRequest(helper.pg, helper.url, helper.pos, helper.method, helper.form, helper.notify, helper.matter, this::callOnFinish);
            queue.remove(0);
        }
    }

    private void addToQueue(int pg, String url, int pos, int method, Map<String, String> form, boolean notify, Matter matter) {
        boolean isNew = true;

        for (RequestHelper h : queue)
            if (h.pg == pg && h.pos == pos) {
                isNew = false;
                break;
            }

        if (isNew) {
            queue.add(new RequestHelper(pg, url, pos, method, form, notify, matter));
            Log.i(TAG, "Queued: " + pg + " for " + User.getYears()[pos]);
        }
    }

    private void fetchParams(Response.Listener<String> listener) {
        addRequest(new StringRequest(GET, URL + GERADOR,
                response ->  {
                    try {
                        String keys = response.substring(response.indexOf("RSAKeyPair("), response.lastIndexOf(")"));
                        keys = keys.substring(keys.indexOf("\"") + 1, keys.lastIndexOf("\""));

                        KEY_A = keys.substring(0, keys.indexOf("\""));
                        Log.d("Key A", KEY_A);
                        KEY_B = keys.substring(keys.lastIndexOf("\"") + 1);
                        Log.d("Key B", KEY_B);

                        Log.v(TAG, "Keys fetched");
                    } catch (IndexOutOfBoundsException ignored) {}

                    listener.onResponse(response);

                }, error -> onError(PG_GENERATOR, error.getMessage() == null ? getContext().getResources().getString(R.string.client_error) : error.getMessage())) {

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.setAcceptCookie(true);

                        String c1 = response.headers.get("Set-Cookie");

                        params.put("Cookie", c1);
                        cookieManager.setCookie(Client.get().getURL(), c1);

                        for (Header h : response.allHeaders) {
                            if (h.getValue().contains("QSESSIONID")) {
                                params.put("Set-Cookie", h.getValue());
                                cookieManager.setCookie(Client.get().getURL(),  h.getValue());
                                break;
                            }
                        }

                        Log.d("Cookies", params.toString());

                        return super.parseNetworkResponse(response);
                    }

                    @Override
                    public Priority getPriority() {
                        return Priority.IMMEDIATE;
                    }

        }, PG_GENERATOR,0);
    }

    public void close() {
        Log.i(TAG, "Logout");
        queue.clear();
        requests.cancelAll(request -> true);
        params.clear();
        onResponses.clear();
        onUpdates.clear();
        KEY_A = "";
        KEY_B = "";
        pos = 0;
        instance = null;
    }

    private void onError(int pg, String msg) {
        if (!isConnected()) {
            msg = getContext().getResources().getString(R.string.client_no_connection);
        } else {
            if (pg == PG_GENERATOR) {
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
        if (onResponse != null && !onResponses.contains(onResponse)) {
            onResponses.add(onResponse);
        }
    }

    public void removeOnResponseListener(OnResponse onResponse) {
        if (onResponses != null && onResponse != null) {
            onResponses.remove(onResponse);
        }
    }

    public void addOnUpdateListener(OnUpdate onUpdate) {
        if (onUpdate != null && !onUpdates.contains(onUpdate)) {
            onUpdates.add(onUpdate);
        }
    }

    public void removeOnUpdateListener(OnUpdate onUpdate) {
        if (onUpdates != null && onUpdate != null) {
            onUpdates.remove(onUpdate);
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
        checkQueue();
    }

    private void callOnAccessDenied(int pg, String message) {
        Log.v(TAG, pg + ": " + message);
        isValid = false;
        isLogging = false;
        queue.clear();
        requests.cancelAll(request -> true);
        params.clear();
        KEY_A = "";
        KEY_B = "";
        pos = 0;
        if (onResponses != null) {
            for (int i = 0; i < onResponses.size(); i++) {
                onResponses.get(i).onAccessDenied(pg, message);
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

    public void changeDate(int pos) {
        if (pos != Client.pos) {
            Client.pos = pos;

            requests.cancelAll(request -> true);

            if (onUpdates != null) {
                for (int i = 0; i < onUpdates.size(); i++) {
                    onUpdates.get(i).onDateChanged();
                }
            }

            loadYear(pos);
        }
    }

    private int posBackup = -1;

    public void restorePreviousDate() {
        if (posBackup >= 0)
            changeDate(posBackup);

        posBackup = -1;
    }

    public void changeDateWithBackup(int pos) {
        if (posBackup == -1)
            posBackup = Client.pos;

        changeDate(pos);
    }

    public void requestScroll() {
        callOnScrollRequest();
    }

    private void downloadImage(String cod) {
        File picture = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + User.getCredential(User.REGISTRATION));

        Log.d("Picture", picture.getAbsolutePath());

        if (!picture.exists()) {
            DownloadReceiver.downloadImage(getContext(), cod);
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

    public String getURL() {
        return URL;
    }

    public String getCookie() {
        return params.get("Cookie");
    }

    public void loadYear(int pos) {
        load(PG_JOURNALS, pos, (pg, year) -> {
                load(PG_REPORT, year, (pg1, year1) -> {
                    load(PG_SCHEDULE, year1, (pg2, year2) -> {
                        for (Matter m : DataBase.get().getBoxStore()
                                .boxFor(Matter.class)
                                .query()
                                .equal(Matter_.year_, User.getYear(year2))
                                .and()
                                .equal(Matter_.period_, User.getPeriod(year2))
                                .build()
                                .find()) {
                            Client.get().load(m);
                        }
                        callOnFinish(pg, year2);
                    });
                    callOnFinish(pg, year1);
                });
            callOnFinish(pg, year);
        });
    }

    public void checkChanges() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean notify = prefs.getBoolean(NOTIFY, true);

        load(PG_JOURNALS, notify, (pg, year) -> {
            load(PG_REPORT, notify, (pg1, year1) -> {
                load(PG_SCHEDULE, notify, (pg2, year2) -> callOnFinish(pg, year2));
                callOnFinish(pg, year1);
            });
            callOnFinish(pg, year);

            load(PG_MESSAGES, notify, (pg1, year1) -> callOnFinish(PG_MESSAGES, 0));
            load(PG_MATERIALS, notify, (pg1, year1) -> callOnFinish(PG_MATERIALS, 0));
        });
    }

}
