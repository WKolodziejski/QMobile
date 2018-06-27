package com.qacademico.qacademico.WebView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.qacademico.qacademico.Class.Infos;
import com.qacademico.qacademico.Utilities.Data;
import com.qacademico.qacademico.Utilities.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.qacademico.qacademico.Utilities.Utils.pg_boletim;
import static com.qacademico.qacademico.Utilities.Utils.pg_diarios;
import static com.qacademico.qacademico.Utilities.Utils.pg_home;
import static com.qacademico.qacademico.Utilities.Utils.pg_horario;
import static com.qacademico.qacademico.Utilities.Utils.pg_login;
import static com.qacademico.qacademico.Utilities.Utils.url;

public class SingletonWebView {
    private static SingletonWebView singleton;
    private OnPageFinished onPageFinished;
    private OnPageStarted onPageStarted;
    private OnRecivedError onRecivedError;
    public WebView html;
    public boolean pg_diarios_loaded, pg_horario_loaded, pg_boletim_loaded, pg_home_loaded, pg_material_loaded,
            pg_login_loaded, pg_calendario_loaded, isChangePasswordPage, isLoginPage;
    public String new_password, bugDiarios, bugCalendario, bugBoletim, bugHorario, scriptDiario = "";
    public int data_position_horario, data_position_boletim, data_position_diarios, periodo_position_horario,
            periodo_position_boletim, periodo_position_calendario;
    public Infos infos;
    private List<String> queue = new ArrayList<>();

    private SingletonWebView() {}

    public synchronized static SingletonWebView getInstance() {
        if (singleton == null) {
            singleton = new SingletonWebView();
        }
        return singleton;
    }

    public void load(String url_i) {
        if (!pg_login_loaded) {
            html.loadUrl(url + pg_login);
            addToQueue(url_i);
        } else if (!pg_home_loaded) {
            html.loadUrl(url + pg_home);
            addToQueue(url_i);
        } else if ((url_i.equals(pg_diarios) && !pg_diarios_loaded)
                || (url_i.equals(pg_boletim) && !pg_boletim_loaded)
                || (url_i.equals(pg_horario) && !pg_horario_loaded)) {
            addToQueue(url_i);
        }
    }

    private void addToQueue(String url_i) {
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).equals(url_i)) {
                queue.remove(i);
            }
        }
        queue.add(url_i);
        html.loadUrl(url + url_i);
    }

    public void nextQueue() {
        if (queue.size() > 0) {
            queue.remove(queue.size() - 1);
        }
        if (queue.size() > 0) {
            html.loadUrl(url + queue.get(queue.size() - 1));
        }
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    public void configWebView(Activity activity) {
        if (Data.loadDate(activity.getApplicationContext()) != null) {
            infos = Data.loadDate(activity.getApplicationContext());
        } else {
            infos = new Infos();
        }

        html = new WebView(activity.getApplicationContext());
        WebSettings faller = html.getSettings();
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

        html.setWebViewClient(clientWebView);
        html.addJavascriptInterface(javaScriptWebView, "HtmlHandler");
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
