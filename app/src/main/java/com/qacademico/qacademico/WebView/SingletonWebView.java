package com.qacademico.qacademico.WebView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class SingletonWebView {
    private static SingletonWebView singleton;
    private OnPageFinished onPageFinished;
    public WebView html;
    public boolean pg_diarios_loaded, pg_horario_loaded, pg_boletim_loaded, pg_home_loaded, pg_material_loaded,
            isChangePasswordPage, isLoginPage;
    public String new_password, bugDiarios, bugBoletim, bugHorario, scriptDiario = "";
    public String[] data_boletim, data_horario, data_diarios, periodo_horario, periodo_boletim;
    public int data_position_horario, data_position_boletim, data_position_diarios, periodo_position_horario,
            periodo_position_boletim;

    private SingletonWebView() {}

    public synchronized static SingletonWebView getInstance() {
        if (singleton == null) {
            singleton = new SingletonWebView();
        }
        return singleton;
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    public void configWebView(Context context) {
        html = new WebView(context.getApplicationContext());
        WebSettings faller = html.getSettings();
        faller.setJavaScriptEnabled(true);
        faller.setDomStorageEnabled(true);
        faller.setLoadsImagesAutomatically(false);
        faller.setUseWideViewPort(true);
        faller.setLoadWithOverviewMode(true);

        JavaScriptWebView javaScriptWebView = new JavaScriptWebView(context);
        javaScriptWebView.setOnPageFinished(url_p -> {
            Log.i("JavaScriptInterface", "onFinish");
            onPageFinished.onPageFinish(url_p);
        });

        ClientWebView clientWebView = new ClientWebView(context);
        clientWebView.setOnPageFinished(url_p -> {
            Log.i("JavaScriptInterface", "onFinish");
            onPageFinished.onPageFinish(url_p);
        });

        html.setWebViewClient(clientWebView);
        html.addJavascriptInterface(javaScriptWebView, "HtmlHandler");
    }

    public void setOnPageFinishedListener(OnPageFinished onPageFinished){
        this.onPageFinished = onPageFinished;
    }

    public interface OnPageFinished {
        void onPageFinish(String url_p);
    }
}
