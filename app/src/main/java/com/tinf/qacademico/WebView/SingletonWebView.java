package com.tinf.qacademico.WebView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.tinf.qacademico.Activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;

import static android.content.Context.MODE_PRIVATE;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_INFO;
import static com.tinf.qacademico.Utilities.Utils.PG_BOLETIM;
import static com.tinf.qacademico.Utilities.Utils.PG_CALENDARIO;
import static com.tinf.qacademico.Utilities.Utils.PG_DIARIOS;
import static com.tinf.qacademico.Utilities.Utils.PG_HOME;
import static com.tinf.qacademico.Utilities.Utils.PG_HORARIO;
import static com.tinf.qacademico.Utilities.Utils.PG_LOGIN;
import static com.tinf.qacademico.Utilities.Utils.PG_MATERIAIS;
import static com.tinf.qacademico.Utilities.Utils.URL;
import static com.tinf.qacademico.Utilities.Utils.YEARS;

public class SingletonWebView {
    private static SingletonWebView singleton;
    private OnPageFinished onPageFinished;
    private OnPageStarted onPageStarted;
    private OnRecivedError onRecivedError;
    private WebView webView;
    private boolean isLoading, isPaused;
    private List<String> queue = new ArrayList<>();
    public boolean[] pg_diarios_loaded = {false},
                     pg_horario_loaded = {false},
                     pg_boletim_loaded = {false},
                     pg_materiais_loaded = {false};
    public boolean isLoginPage, pg_calendario_loaded, pg_home_loaded;
    public String scriptDiario = "", scriptMateriais = "";
    public int year_position;
    public String[] data_year = {""};
    public BoxStore box;

    private SingletonWebView() {}

    public synchronized static SingletonWebView getInstance() {
        if (singleton == null) {
            singleton = new SingletonWebView();
        }
        return singleton;
    }

    public void loadUrl(String pg) {
        if (!pg.contains("javascript")) {
            Log.i("Client", "Não contém java script");
            if (!isLoading) {
                if (pg_home_loaded) {
                    Log.i("Client", "Carregando...");
                    webView.loadUrl(pg);
                } else {
                    webView.loadUrl(URL + PG_LOGIN);
                    addToQueue(pg);
                }
            } else {
                addToQueue(pg);
            }
        } else {
            Log.i("Client", "Contém java script");
            webView.loadUrl(pg);
        }
    }

    public void loadNextUrl() {
        if (!isPaused) {
            if (queue.size() > 0) {
                String queue = this.queue.get(this.queue.size() - 1);
                this.queue.remove(this.queue.size() - 1);

                if (!queue.isEmpty()) {
                    Log.i("Client", "Carregando fila...");
                    loadUrl(queue);
                }
            }
        }
    }

    public void resumeQueue() {
        isPaused = false;
    }

    private void addToQueue(String pg) {
        boolean equals = false;
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).equals(pg)) {
                equals = true;
                break;
            }
        }
        if (!equals) {
            Log.i("Client", "add a fila: " + pg);
            queue.add(pg);
        }
    }

    public void downloadMaterial(Context context, String link){
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        });
        webView.loadUrl("javascript:document.querySelector(\"a[href='" + link + "']\").click();");
    }

    public void changeDate(Context context, int value) {
        if (!isLoading) {
            year_position = value;

            scriptMateriais = "javascript: document.getElementById(\"ANO_PERIODO\").selectedIndex ="
                    + (year_position + 1) + ";document.forms[0].submit();";

            webView.loadUrl(URL + PG_MATERIAIS);

        } else {
            Toast.makeText(context, "Espere terminar de carregar", Toast.LENGTH_SHORT).show();
        }
    }

    private String[] loadYears(Context context) {
        List<String> years = new ArrayList<>();
        String jsonArrayString = context.getSharedPreferences(LOGIN_INFO, MODE_PRIVATE).getString(YEARS, "");
        if (!TextUtils.isEmpty(jsonArrayString)) {
            try {
                JSONArray jsonArray = new JSONArray(jsonArrayString);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        years.add(jsonArray.getString(i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return years.toArray(new String[0]);
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    public void configWebView(Context context) {

        data_year = loadYears(context);

        //queue.add(URL + PG_CALENDARIO);
        //queue.add(URL + PG_HORARIO);
        queue.add(URL + PG_BOLETIM);
        queue.add(URL + PG_DIARIOS);
        //queue.add(URL + PG_HOME);

        webView = new WebView(context);
        WebSettings faller = webView.getSettings();
        faller.setJavaScriptEnabled(true);
        faller.setDomStorageEnabled(true);
        faller.setLoadsImagesAutomatically(false);
        faller.setUseWideViewPort(true);
        faller.setLoadWithOverviewMode(true);

        JavaScriptWebView javaScriptWebView = new JavaScriptWebView(context);

        javaScriptWebView.setOnPageFinished((url_p, list) -> {
            isLoading = false;
            if (url_p.contains(URL + PG_MATERIAIS)) {
                isPaused = true;
            }
            Log.i("Finish", url_p);
            onPageFinished.onPageFinish(url_p, list);
        });

        ClientWebView clientWebView = new ClientWebView(context);

        clientWebView.setOnPageFinishedListener(url_p -> {
            isLoading = false;
            Log.i("Finish", url_p);
            onPageFinished.onPageFinish(url_p, null);
        });

        clientWebView.setOnPageStartedListener(url_p -> {
            isLoading = true;
            Log.i("Start", url_p);
            onPageStarted.onPageStart(url_p);
        });

        clientWebView.setOnErrorRecivedListener(error -> {
            isLoading = false;
            Log.i("Error", error);
            onRecivedError.onErrorRecived(error);
        });

        webView.setWebViewClient(clientWebView);
        webView.addJavascriptInterface(javaScriptWebView, "HtmlHandler");
    }

    public synchronized static void logOut() {
        singleton = new SingletonWebView();
    }

    public void setBoxStore(BoxStore box) {
        this.box = box;
        Log.i("BoxStore", box.toString());
    }

    public void setOnPageFinishedListener(OnPageFinished onPageFinished){
        this.onPageFinished = onPageFinished;
    }

    public interface OnPageFinished {
        void onPageFinish(String url_p, List<?> list);
    }

    public void setOnPageStartedListener(OnPageStarted onPageStarted){
        this.onPageStarted = onPageStarted;
    }

    public interface OnPageStarted {
        void onPageStart(String url_p);
    }

    public void setOnErrorRecivedListener(OnRecivedError onRecivedError){
        this.onRecivedError = onRecivedError;
    }

    public interface OnRecivedError {
        void onErrorRecived(String error);
    }
}
