package com.qacademico.qacademico.Application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.qacademico.qacademico.Activity.MainActivity;
import com.qacademico.qacademico.R;

import java.util.concurrent.Executor;

/*
 * Esta classe fornece variáveis e funções que estão disponíveis globalmente
 */

public class MainApplication extends Application {
    private static MainApplication singleton;
    public WebView html;

    public static String url, pg_login, pg_home, pg_diarios, pg_boletim, pg_horario, pg_materiais, pg_change_password,
            pg_erro, download_update_url, email_to, email_from, email_from_pwd;
    FirebaseRemoteConfig remoteConfig;

    /*
     * Referencia a classe como um objeto único
     */
    public static MainApplication getInstance(){
        return singleton;
    }

    /*
     * Método chamado ao iniciar o app
     */
    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;

        setDefaultHashMap();
        updateDefaultValues();

        html = new WebView(this);
        WebSettings faller = html.getSettings();
        faller.setJavaScriptEnabled(true);
        faller.setDomStorageEnabled(true);
        faller.setLoadsImagesAutomatically(false);
        faller.setUseWideViewPort(true);
        faller.setLoadWithOverviewMode(true);
    }

    /*
     * Método que recebe os valores do servidor remoto
     */
    private void setDefaultHashMap() {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build());

        remoteConfig.setDefaults(R.xml.default_values);

        final Task<Void> fetch = remoteConfig.fetch(0);
        fetch.addOnSuccessListener(command -> {
            remoteConfig.activateFetched();
            updateDefaultValues();
            Log.v("DefaultValues", "Valores atualizados");
        });

        fetch.addOnFailureListener(e -> {
            Log.v("DefaultValues", "Erro");
        });
    }

    /*
     * Método que atualiza os valores das variáveis
     */
    public void updateDefaultValues() {
        url = remoteConfig.getString("default_url");
        pg_login = remoteConfig.getString("pg_login");
        pg_home = remoteConfig.getString("pg_home");
        pg_diarios = remoteConfig.getString("pg_diarios");
        pg_boletim = remoteConfig.getString("pg_boletim");
        pg_horario = remoteConfig.getString("pg_horario");
        pg_materiais = remoteConfig.getString("pg_materiais");
        pg_change_password = remoteConfig.getString("pg_change_password");
        pg_erro = remoteConfig.getString("pg_erro");
        download_update_url = remoteConfig.getString("download_update_url");
        email_to = remoteConfig.getString("email_to");
        email_from = remoteConfig.getString("email_from");
        email_from_pwd = remoteConfig.getString("email_from_pass");
    }

}
