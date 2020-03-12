package com.tinf.qmobile.network;

import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
import com.tinf.qmobile.fragment.OnUpdate;
import com.tinf.qmobile.model.material.Material;
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
import static com.tinf.qmobile.network.OnResponse.IFMT;
import static com.tinf.qmobile.network.OnResponse.INDEX;
import static com.tinf.qmobile.network.OnResponse.PG_ACESSO_NEGADO;
import static com.tinf.qmobile.network.OnResponse.PG_BOLETIM;
import static com.tinf.qmobile.network.OnResponse.PG_CALENDARIO;
import static com.tinf.qmobile.network.OnResponse.PG_CLASSES;
import static com.tinf.qmobile.network.OnResponse.PG_DIARIOS;
import static com.tinf.qmobile.network.OnResponse.PG_FETCH_YEARS;
import static com.tinf.qmobile.network.OnResponse.PG_GERADOR;
import static com.tinf.qmobile.network.OnResponse.PG_HOME;
import static com.tinf.qmobile.network.OnResponse.PG_HORARIO;
import static com.tinf.qmobile.network.OnResponse.PG_LOGIN;
import static com.tinf.qmobile.network.OnResponse.PG_MATERIAIS;
import static com.tinf.qmobile.utility.User.REGISTRATION;

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

    private void createRequest(int pg, String url, int pos, int method, Map<String, String> form, boolean notify, Matter matter, OnFinish onFinish) {
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
                                new JournalParser(pos, notify, onFinish).execute(response);

                            } else if (pg == PG_BOLETIM) {
                                new ReportParser(pos, onFinish).execute(response);

                            } else if (pg == PG_HORARIO) {
                                new ScheduleParser(pos, onFinish).execute(response);

                            } else if (pg == PG_MATERIAIS) {
                                new MateriaisParser(pos, notify, onFinish).execute(response);

                            } else if (pg == PG_CALENDARIO) {
                                new CalendarParser(onFinish).execute(response);

                            } else if (pg == PG_CLASSES) {
                                new ClassParser(matter, pg, pos, notify, onFinish, this::callOnError).execute(response);

                            } else if (pg == PG_FETCH_YEARS) {
                                Document document = Jsoup.parse(response);

                                Elements dates = document.getElementsByTag("option");

                                String[] years = new String[dates.size() - 1];

                                for (int i = 0; i < dates.size() - 1; i++) {
                                    years[i] = dates.get(i + 1).text();
                                }

                                User.setYears(years);

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

    public void load(int pg, int pos) {
        load(pg, pos, this::callOnFinish);
    }

    public void load(Matter matter) {
        createRequest(PG_CLASSES,
                INDEX + PG_DIARIOS + "&ACAO=VER_FREQUENCIA&COD_PAUTA=" + matter.getQID() + "&ANO_PERIODO=" + matter.getYear_() + "_" + matter.getPeriod_(),
                pos, POST, new HashMap<>(), false, matter, this::callOnFinish);
    }

    public void load(int pg, int pos, OnFinish onFinish) {
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

        createRequest(pg, url, pos, method, form, false, null, onFinish);
    }

    private <T> void addRequest(Request<T> request, int pg, int pos) {
        if (isConnected()) {
            callOnStart(pg, pos);
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

                        //String cod = document.getElementsByTag("q_latente").get(4).val();
                        //cod = cod.substring(cod.indexOf("=") + 1);

                        //downloadImage(cod);

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

    /*public void login(Context context) {
        callOnStart(PG_LOGIN, pos);

        isLogging = true;

        webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavaScriptHandler(), "handler");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Log.d("Webview", url);

                if (url.contains(String.valueOf(PG_HOME))) {
                        webView.loadUrl("javascript:window.handler.handleLogin"
                                + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                callOnError(PG_HOME, error.toString());
            }

        });

        fetchParams(response -> {
            String post =  User.getLoginParams(KEY_A, KEY_B).toString();
            post = post.replace("{", "");
            post = post.replace(", ", "&");
            post = post.replace("}", "");

            Log.d("POST", post);

            webView.postUrl(Client.get().getURL() + VALIDA, post.getBytes());
        });
    }*/

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
                        //for(Header h : response.allHeaders)
                            //Log.d(h.getName(), h.getValue());

                        /*int i = 5;
                        int j = 6;

                        if (User.getURL().equals(IFMT)) {
                            i--;
                            j--;
                        }

                        params.put("Set-Cookie", response.allHeaders.get(i).getValue());
                        params.put("Cookie", response.allHeaders.get(j).getValue());*/

                        //Log.d("Cookie", response.headers.get("Set-Cookie"));

                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.setAcceptCookie(true);

                        String c1 = response.headers.get("Set-Cookie");

                        params.put("Cookie", c1);
                        cookieManager.setCookie(Client.get().getURL(), c1);

                        for (Header h : response.allHeaders) {
                            if (h.getValue().contains("QSESSIONID")) {
                                params.put("Set-Cookie", h.getValue());
                                cookieManager.setCookie(Client.get().getURL(),  h.getValue());
                            }
                        }

                        Log.d("Cookies", params.toString());

                        return super.parseNetworkResponse(response);
                    }

                    @Override
                    public Priority getPriority() {
                        return Priority.IMMEDIATE;
                    }

        }, PG_GERADOR,0);
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
        if (onResponse != null && !onResponses.contains(onResponse)) {
            onResponses.add(onResponse);
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
        if (onUpdate != null && !onUpdates.contains(onUpdate)) {
            onUpdates.add(onUpdate);
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

    public void changeData(int pos) {
        Client.pos = pos;

        requests.cancelAll(request -> true);

        if (onUpdates != null) {
            for (int i = 0; i < onUpdates.size(); i++) {
                onUpdates.get(i).onDateChanged();
            }
        }
    }

    public void requestScroll() {
        callOnScrollRequest();
    }

    public DownloadManager.Request downloadMaterial(Material material) {
        Uri uri = Uri.parse(URL + material.getLink());

        return new DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverRoaming(false)
                .setTitle(material.getTitle())
                .setDescription(material.getDateString())
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        "QMobile/" + User.getCredential(REGISTRATION) + "/" + User.getYear(pos) + "/" + User.getPeriod(pos)
                                + "/" + material.getFileName());
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
                    .setDestinationInExternalFilesDir(getContext(), Environment.DIRECTORY_PICTURES, User.getCredential(User.REGISTRATION)));
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

    public void loadYear(int pos) {
        load(PG_DIARIOS, pos, (pg, year) -> {
                load(PG_BOLETIM, year, (pg1, year1) -> {
                    load(PG_HORARIO, year1, (pg2, year2) -> {
                        callOnFinish(pg, year2);
                    });
                    callOnFinish(pg, year1);
                });
            callOnFinish(pg, year);
        });
    }

    public interface OnFinish {
        void onFinish(int pg, int year);
    }

    /*public class JavaScriptHandler {

        @JavascriptInterface
        public void handleLogin(String page) {
            Client.Resp r = testResponse(page);

            if (r == Client.Resp.DENIED) {
                callOnAccessDenied(PG_LOGIN, "");

            } else if (r == Client.Resp.OK) {
                isValid = true;

                Document document = Jsoup.parse(page);
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

                Element body = document.getElementById("modalmensagens");

                document.outputSettings(new Document.OutputSettings().prettyPrint(false));
                document.select("br").after("\\n");

                if (body != null) {
                    String title = body.getElementsByClass("subtitulo").first().text().replaceAll("\\\\n", "\n").trim();
                    String message = body.getElementsByClass("conteudoTexto").first().getElementsByTag("p").first().text().replaceAll("\\\\n", "\n").trim();

                    callOnDialog(webView, title, message);
                }

                callOnFinish(PG_LOGIN, 0);

            }

        }
    }*/

}
