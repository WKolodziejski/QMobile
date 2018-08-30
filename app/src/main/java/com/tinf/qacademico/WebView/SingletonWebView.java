package com.tinf.qacademico.WebView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.tinf.qacademico.Class.Infos;
import com.tinf.qacademico.Utilities.Data;

import java.util.ArrayList;
import java.util.List;

import static com.tinf.qacademico.Utilities.Utils.PG_BOLETIM;
import static com.tinf.qacademico.Utilities.Utils.PG_CALENDARIO;
import static com.tinf.qacademico.Utilities.Utils.PG_DIARIOS;
import static com.tinf.qacademico.Utilities.Utils.PG_HORARIO;
import static com.tinf.qacademico.Utilities.Utils.PG_MATERIAIS;
import static com.tinf.qacademico.Utilities.Utils.URL;

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
    public String scriptDiario = "";
    public int data_position_horario, data_position_boletim, data_position_diarios, data_position_materiais, periodo_position_horario,
            periodo_position_boletim;
    public Infos infos;

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
                Log.i("Client", "Carregando...");
                webView.loadUrl(pg);
            } else {
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

    public void downloadMaterial(Context context, String link){
        if (!webView.getUrl().contains(URL + PG_MATERIAIS)) {
            loadUrl(URL + PG_MATERIAIS);
        }
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        });
        webView.loadUrl("javascript:document.querySelector(\"a[href='" + link + "']\").click();");
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    public void configWebView(Context context) {
        if (Data.loadDate(context) != null) {
            infos = Data.loadDate(context);
        } else {
            infos = new Infos();
        }

        queue.add(URL + PG_CALENDARIO);
        queue.add(URL + PG_HORARIO);
        queue.add(URL + PG_BOLETIM);
        queue.add(URL + PG_DIARIOS);

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
            Log.i("JavaScriptInterface", "onFinish");
            onPageFinished.onPageFinish(url_p, list);
        });

        ClientWebView clientWebView = new ClientWebView(context);

        clientWebView.setOnPageFinishedListener(url_p -> {
            isLoading = false;
            Log.i("Client", "onFinish");
            onPageFinished.onPageFinish(url_p, null);
        });

        clientWebView.setOnPageStartedListener(url_p -> {
            isLoading = true;
            Log.i("Client", "onStart");
            onPageStarted.onPageStart(url_p);
        });

        clientWebView.setOnErrorRecivedListener(error -> {
            isLoading = false;
            Log.i("Client", "onError");
            onRecivedError.onErrorRecived(error);
        });

        webView.setWebViewClient(clientWebView);
        webView.addJavascriptInterface(javaScriptWebView, "HtmlHandler");
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