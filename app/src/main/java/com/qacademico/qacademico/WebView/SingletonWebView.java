package com.qacademico.qacademico.WebView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
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
        javaScriptWebView.onPageFinished(url_p -> {
            Log.i("JavaScriptInterface", "onFinish");
            onPageFinished.OnPageFinish(url_p);
        });

        html.setWebViewClient(new ClientWebView(context));
        html.addJavascriptInterface(javaScriptWebView, "HtmlHandler");
    }

    public synchronized static SingletonWebView getInstance() {
        if (singleton == null) {
            singleton = new SingletonWebView();
        }
        return singleton;
    }

    public void onPageFinished(OnPageFinished onPageFinished){
        this.onPageFinished = onPageFinished;
    }

    public interface OnPageFinished {
        void OnPageFinish(String url_p);
    }
}
