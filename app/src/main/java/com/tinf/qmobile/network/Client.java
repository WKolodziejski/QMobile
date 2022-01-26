package com.tinf.qmobile.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
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
import com.google.firebase.crashlytics.FirebaseCrashlytics;
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
import java.nio.charset.StandardCharsets;
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
import static com.tinf.qmobile.network.OnResponse.PG_QUEST;
import static com.tinf.qmobile.network.OnResponse.PG_REPORT;
import static com.tinf.qmobile.network.OnResponse.PG_SCHEDULE;
import static com.tinf.qmobile.network.OnResponse.PG_UPDATE;

public class Client {
    private final static String TAG = "Network Client";
    private final static String GERADOR = "/qacademico/lib/rsa/gerador_chaves_rsa.asp";
    private final static String VALIDA = "/qacademico/lib/validalogin.asp";
    private static Client instance;
    private final List<RequestHelper> queue;
    private String URL;
    private final List<OnResponse> onResponses;
    private final List<OnUpdate> onUpdates;
    private final RequestQueue requests;
    private final Map<String, String> params;
    private String KEY_A;
    private String KEY_B;
    public static int pos = 0;
    public static boolean background = false;
    private boolean isValid;
    private boolean isLogging;

    public enum Resp {
        OK, HOST, DENIED, EGRESS, UPDATE, QUEST, UNKNOWN
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

    private void createRequest(int pg, String url, int year, int period, int method, Map<String, String> form, boolean notify, Matter matter, BaseParser.OnFinish onFinish) {
        if (!isValid) {
            if (!isLogging) {
                login();
            }
            addToQueue(pg, url, year, period, method, form, notify, matter);
        } else {
            Log.i(TAG, "Request for: " + pg);
            addRequest(new StringRequest(method, URL + url, responseASCII -> {
                String response = new String(responseASCII.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

                Resp r = testResponse(response);

                if (r == Resp.DENIED) {
                    addToQueue(pg, url, year, period, method, form, notify, matter);
                    login();
                    callOnAccessDenied(pg, getContext().getResources().getString(R.string.login_expired));

                } else if (r == Resp.OK) {
                    if (pg == PG_JOURNALS) {
                        new JournalParser(pg, year, period, notify, onFinish, this::callOnError).execute(response);

                    } else if (pg == PG_REPORT) {
                        new ReportParser(pg, year, period, notify, onFinish, this::callOnError).execute(response);

                    } else if (pg == PG_SCHEDULE) {
                        new ScheduleParser(pg, year, period, notify, onFinish, this::callOnError).execute(response);

                    } else if (pg == PG_MATERIALS) {
                        new MaterialsParser(pg, year, period, notify, onFinish, this::callOnError).execute(response);

                    } else if (pg == PG_CALENDAR) {
                        new CalendarParser(pg, year, period, notify, onFinish, this::callOnError).execute(response);

                    } else if (pg == PG_MESSAGES) {
                        new MessageParser(pg, year, period, notify, onFinish, this::callOnError).execute(response);

                    } else if (pg == PG_CLASSES) {
                        new ClassParser(matter, pg, year, period, notify, onFinish, this::callOnError).execute(response);

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

                        callOnFinish(PG_FETCH_YEARS);

                        loadYear(0);
                    }
                }
            }, error -> onError(pg, error.getMessage() == null ?
                    getContext().getResources().getString(R.string.client_error) : error.getMessage())) {

                @Override
                public Map<String, String> getHeaders() {
                    return params;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return !form.isEmpty() ? form : super.getParams();
                }

            }, pg);
        }
    }

    public void load(int pg) {
        load(pg, User.getYear(pos), User.getPeriod(pos), this::callOnFinish);
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
                matter.getYear_(), matter.getPeriod_(), POST, new HashMap<>(), false, matter, this::callOnFinish);
    }

    private void load(int pg, int year, int period, BaseParser.OnFinish onFinish) {
        load(pg, year, period, onFinish, false);
    }

    private void load(int pg, boolean notify, BaseParser.OnFinish onFinish) {
        load(pg, User.getYear(pos), User.getPeriod(pos), onFinish, notify);
    }

    private void load(int pg, int year, int period, BaseParser.OnFinish onFinish, boolean notify) {
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
                    form.put("ANO_PERIODO2", year + "_" + period);
                    break;

                case PG_REPORT:

                case PG_SCHEDULE:
                    method = GET;
                    url = url.concat("&cmbanos=" + year + "&cmbperiodos=" + period);
                    break;

                case PG_MATERIALS: method = POST;
                    form.put("ANO_PERIODO", year + "_" + period);
                    break;
            }
        }

        createRequest(pg, url, year, period, method, form, notify, null, onFinish);
    }

    private <T> void addRequest(Request<T> request, int pg) {
        if (isConnected()) {
            callOnStart(pg);
            request.setRetryPolicy(new DefaultRetryPolicy());
            requests.add(request);
            Log.v(TAG, "Loading: " + request);
        } else {
            onError(pg, getContext().getResources().getString(R.string.client_no_connection));
        }
    }

    public void login() {
        isLogging = true;
        fetchParams(success -> addRequest(new StringRequest(POST, URL + VALIDA, responseASCII -> {
            String response = new String(responseASCII.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

            Resp r = testResponse(response);

            if (r == Resp.DENIED) {
                callOnAccessDenied(PG_LOGIN, getContext().getString(R.string.login_invalid));

            } else if (r == Resp.OK) {

                isValid = true;

                Document document = Jsoup.parse(response);

                User.setLastLogin(new Date().getTime());

                isLogging = false;

                String cod = document.getElementsByTag("q_latente").get(4).val();
                cod = cod.substring(cod.indexOf("=") + 1);

                Element img = document.getElementsByAttributeValueEnding("src", cod).first();

                if (img != null && !background)
                    downloadImage(cod);

                callOnFinish(PG_LOGIN);
            }

        }, error -> onError(PG_LOGIN, error.getMessage() == null ?
                getContext().getResources().getString(R.string.client_error) : error.getMessage())) {

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

        }, PG_LOGIN));
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
        }

        Element p = document.getElementsByTag("p").first();

        if (p != null) {
            if (p.text().contains("inacessível")) {
                callOnError(PG_LOGIN, getContext().getResources().getString(R.string.client_host));
                return Resp.HOST;
            }
        }

        Element form = document.getElementsByClass("conteudoTexto").first();

        if (form != null) {
            if (form.text().contains("senha")) {
                String msg = form.text().replaceAll("\\\\n", "\n").trim();
                callOnAccessDenied(PG_UPDATE, msg);
                return Resp.UPDATE;
            }
        }

        Element quest = document.getElementsByClass("TEXTO_TITULO").first();

        if (quest != null) {
            if (quest.text().contains("Questionários")) {
                String msg = "";
                if (form != null) {
                    msg = form.text().replaceAll("\\\\n", "\n").trim();
                }
                callOnAccessDenied(PG_QUEST, msg);
                return Resp.QUEST;
            }
        }

        Elements sub = document.getElementsByClass("barraRodape");
        Elements sub2 = document.getElementsByClass("titulo");

        String name;

        if (sub.size() >= 2) {
            name = sub.get(1).text();
        } else if (sub2.size() >= 2) {
            name = sub2.get(1).text();
            name = name.substring(name.indexOf(",") + 1).trim();
        } else {
            callOnAccessDenied(0, getContext().getString(R.string.client_error));
            FirebaseCrashlytics.getInstance().recordException(new Exception(document.toString()));
            return Resp.UNKNOWN;
        }

        User.setName(name);

        return Resp.OK;
    }

    private void checkQueue() {
        while (!queue.isEmpty()) {
            RequestHelper helper = queue.get(0);
            createRequest(helper.pg, helper.url, helper.year, helper.period, helper.method, helper.form, helper.notify, helper.matter, this::callOnFinish);
            queue.remove(0);
        }
    }

    private void addToQueue(int pg, String url, int year, int period, int method, Map<String, String> form, boolean notify, Matter matter) {
        boolean isNew = true;

        for (RequestHelper h : queue)
            if (h.pg == pg && h.year == year && h.period == period) {
                isNew = false;
                break;
            }

        if (isNew) {
            queue.add(new RequestHelper(pg, url, year, period, method, form, notify, matter));
            //Log.i(TAG, "Queued: " + pg + " for " + User.getYears()[pos]);
        }
    }

    private void fetchParams(Response.Listener<String> listener) {
        addRequest(new StringRequest(GET, URL + GERADOR, responseASCII ->  {
            String response = new String(responseASCII.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

            try {
                String keys = response.substring(response.indexOf("RSAKeyPair("), response.lastIndexOf(")"));
                keys = keys.substring(keys.indexOf("\"") + 1, keys.lastIndexOf("\""));

                KEY_A = keys.substring(0, keys.indexOf("\""));
                Log.d("Key A", KEY_A);
                KEY_B = keys.substring(keys.lastIndexOf("\"") + 1);
                Log.d("Key B", KEY_B);

                Log.v(TAG, "Keys fetched");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }

            listener.onResponse(response);

        }, error -> onError(PG_GENERATOR, error.getMessage() == null ?
                getContext().getResources().getString(R.string.client_error) : error.getMessage())) {

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

                //params.put("Accept", "text/html");
                //params.put("Content-Type", "text/html; charset=utf-8");

                Log.d("Cookies", params.toString());

                return super.parseNetworkResponse(response);
            }

            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }

        }, PG_GENERATOR);
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
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
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
            Log.v(TAG, "addOnUpdateListener: " + onUpdate);
        }
    }

    public void removeOnUpdateListener(OnUpdate onUpdate) {
        if (onUpdates != null && onUpdate != null) {
            onUpdates.remove(onUpdate);
            Log.v(TAG, "removeOnUpdateListener: " + onUpdate);
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

    private void callOnStart(int pg) {
        Log.v(TAG, "Start: " + pg);
        if (onResponses != null) {
            for (int i = 0; i < onResponses.size(); i++) {
                onResponses.get(i).onStart(pg);
            }
        }
    }

    private void callOnFinish(int pg) {
        Log.v(TAG, "Finish: " + pg);
        if (onResponses != null) {
            for (int i = 0; i < onResponses.size(); i++) {
                onResponses.get(i).onFinish(pg);
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
        //pos = 0;
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

    public void requestDelayedUpdate() {
        new Handler().postDelayed(() -> {
            if (onUpdates != null) {
                for (int i = 0; i < onUpdates.size(); i++) {
                    onUpdates.get(i).onDateChanged();
                }
            }
        }, 10);
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
        int year = User.getYear(pos);
        int period = User.getPeriod(pos);
        load(PG_JOURNALS, year, period, pg -> {
                load(PG_REPORT, year, period, pg1 -> {
                    load(PG_SCHEDULE, year, period, pg2 -> {
                        for (Matter m : DataBase.get().getBoxStore()
                                .boxFor(Matter.class)
                                .query()
                                .equal(Matter_.year_, year)
                                .and()
                                .equal(Matter_.period_, period)
                                .build()
                                .find()) {
                            Client.get().load(m);
                        }
                        callOnFinish(pg2);
                    });
                    callOnFinish(pg1);
                });
            callOnFinish(pg);
        });
    }

    public void checkChanges() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean notify = prefs.getBoolean(NOTIFY, true);

        load(PG_JOURNALS, notify, pg -> {
            load(PG_REPORT, notify, pg1 -> {
                load(PG_SCHEDULE, notify, pg2 -> callOnFinish(pg));
                callOnFinish(pg);
            });
            callOnFinish(pg);

            load(PG_MESSAGES, notify, (pg1) -> callOnFinish(PG_MESSAGES));
            load(PG_MATERIALS, notify, (pg1) -> callOnFinish(PG_MATERIALS));
        });
    }

}
