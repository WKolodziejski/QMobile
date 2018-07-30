package com.qacademico.qacademico.WebView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.qacademico.qacademico.Class.Infos;
import com.qacademico.qacademico.Utilities.Data;

import java.util.List;

import static com.qacademico.qacademico.Utilities.Utils.URL;

public class SingletonWebView {
    private static SingletonWebView singleton;
    private OnPageFinished onPageFinished;
    private OnPageStarted onPageStarted;
    private OnRecivedError onRecivedError;
    private WebView webView;
    public boolean[] pg_diarios_loaded = {false},
                     pg_horario_loaded = {false},
                     pg_boletim_loaded = {false},
                     pg_materiais_loaded = {false};
    public boolean isChangePasswordPage, isLoginPage, pg_login_loaded, pg_calendario_loaded, pg_home_loaded;
    public String new_password, bugDiarios, bugCalendario, bugBoletim, bugHorario, scriptDiario = "";
    public int data_position_horario, data_position_boletim, data_position_diarios, data_position_materiais, periodo_position_horario,
            periodo_position_boletim, periodo_position_calendario;
    public Infos infos;

    private SingletonWebView() {}

    public synchronized static SingletonWebView getInstance() {
        if (singleton == null) {
            singleton = new SingletonWebView();
        }
        return singleton;
    }

    public void loadUrl(String pg) {
        webView.loadUrl(pg);
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    public void configWebView(Activity activity) {
        if (Data.loadDate(activity.getApplicationContext()) != null) {
            infos = Data.loadDate(activity.getApplicationContext());
        } else {
            infos = new Infos();
        }

        webView = new WebView(activity.getApplicationContext());
        WebSettings faller = webView.getSettings();
        faller.setJavaScriptEnabled(true);
        faller.setDomStorageEnabled(true);
        faller.setLoadsImagesAutomatically(false);
        faller.setUseWideViewPort(true);
        faller.setLoadWithOverviewMode(true);

        JavaScriptWebView javaScriptWebView = new JavaScriptWebView(activity);
        javaScriptWebView.setOnPageFinished((url_p, list) -> {
            Log.i("JavaScriptInterface", "onFinish");
            onPageFinished.onPageFinish(url_p, list);
        });

        ClientWebView clientWebView = new ClientWebView(activity.getApplicationContext());
        clientWebView.setOnPageFinishedListener(url_p -> {
            Log.i("JavaScriptInterface", "onFinish");
            onPageFinished.onPageFinish(url_p, null);
        });

        clientWebView.setOnPageStartedListener(url_p -> {
            Log.i("JavaScriptInterface", "onStart");
            onPageStarted.onPageStart(url_p);
        });

        clientWebView.setOnErrorRecivedListener(error -> {
            Log.i("JavaScriptInterface", "onError");
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
