package com.qacademico.qacademico.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.qacademico.qacademico.Fragment.LoginFragment;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;

import java.util.List;

import static com.qacademico.qacademico.Utilities.Utils.PG_BOLETIM;
import static com.qacademico.qacademico.Utilities.Utils.PG_DIARIOS;
import static com.qacademico.qacademico.Utilities.Utils.PG_ERRO;
import static com.qacademico.qacademico.Utilities.Utils.PG_HORARIO;
import static com.qacademico.qacademico.Utilities.Utils.PG_LOGIN;
import static com.qacademico.qacademico.Utilities.Utils.URL;

public class LoginActivity extends AppCompatActivity implements SingletonWebView.OnPageFinished {
    public SingletonWebView webView = SingletonWebView.getInstance();
    LoginFragment loginFragment = new LoginFragment();
    public Snackbar snackBar;
    ViewGroup loginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginLayout = (ViewGroup) findViewById(R.id.login_container);

        webView.isLoginPage = true;
        webView.setOnPageFinishedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment, loginFragment, Utils.LOGIN).commit();
    }

    @Override
    public void onPageFinish(String url_p, List<?> list) {
        runOnUiThread(() -> {
            if (url_p.equals(URL + PG_LOGIN)) {
                if (loginFragment.login_info.getBoolean(Utils.FIRST_LOGIN, true)) {
                    firstLogin();
                } else {
                    SharedPreferences.Editor editor = loginFragment.login_info.edit();
                    editor.putBoolean(Utils.LOGIN_VALID, true);
                    editor.apply();
                    finish();
                }
            } else if (url_p.equals(URL + PG_ERRO)) {
                loginFragment.dismissProgressBar();
                showSnackBar(getResources().getString(R.string.text_invalid_login), false);
            } else if (url_p.contains(URL + PG_BOLETIM) || url_p.contains(URL + PG_HORARIO) || url_p.contains(URL + PG_DIARIOS)) {
                firstLogin();
            }
        });
    }

    public void showSnackBar(String message, boolean action) { //Mostra a SnackBar
        snackBar = Snackbar.make(loginLayout, message, Snackbar.LENGTH_INDEFINITE);
        snackBar.setActionTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLight)));
        if (action) {
            snackBar.setAction(R.string.button_wifi, view1 -> {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                snackBar.dismiss();
            });
        }
        snackBar.show();
    }

    public void dismissSnackbar() { //Esconde a SnackBar
        if (null != snackBar) {
            snackBar.dismiss();
            snackBar = null;
        }
    }

    private void firstLogin() {
        Log.i("LoginActivity", "FirstLogin()");

        loginFragment.textView_loading.setVisibility(View.VISIBLE);

        if (!webView.pg_boletim_loaded[0]) {
            webView.loadUrl(URL + PG_BOLETIM);
            Log.i("LoginActivity", "BOLETIM[0]");
            return;
        } else {
            for (int i = 1; i < webView.infos.data_boletim.length; i++) {
                if (!webView.pg_boletim_loaded[i]) {
                    Log.i("LoginActivity", "BOLETIM[" + i + "]");
                    webView.data_position_boletim = i;

                    webView.loadUrl(URL + PG_BOLETIM + "&COD_MATRICULA=-1&cmbanos="
                            + webView.infos.data_boletim[i] + "&cmbperiodos=1&Exibir+Boletim");

                    loginFragment.textView_loading.setText(String.format(
                            getResources().getString(R.string.text_loading_first_login),
                            Integer.toString(i + 1), Integer.toString(webView.infos.data_boletim.length)));

                    return;
                }
            }
        }

        webView.data_position_boletim = 0;

        if (!webView.pg_horario_loaded[0]) {
            webView.loadUrl(URL + PG_HORARIO);
            Log.i("LoginActivity", "HORARIO[0]");
            return;
        } else {
            for (int i = 1; i < webView.infos.data_horario.length; i++) {
                if (!webView.pg_horario_loaded[i]) {
                    Log.i("LoginActivity", "HORARIO[" + i + "]");
                    webView.data_position_horario = i;

                    webView.loadUrl(URL + PG_HORARIO + "&COD_MATRICULA=-1&cmbanos=" +
                            webView.infos.data_horario[i] + "&cmbperiodos=1&Exibir=OK");

                    loginFragment.textView_loading.setText(String.format(
                            getResources().getString(R.string.text_loading_first_login),
                            Integer.toString(i + 1), Integer.toString(webView.infos.data_horario.length)));

                    return;
                }
            }
        }

        webView.data_position_horario = 0;

        if (!webView.pg_diarios_loaded[0]) {
            webView.loadUrl(URL + PG_DIARIOS);
            Log.i("LoginActivity", "DIARIOS[0]");
            return;
        } else {
            for (int i = 1; i < webView.infos.data_diarios.length; i++) {
                if (!webView.pg_diarios_loaded[i]) {
                    Log.i("LoginActivity", "DIARIOS[" + i + "]");
                    webView.data_position_diarios = i;

                    webView.scriptDiario = "javascript: var option = document.getElementsByTagName('option'); option["
                            + (webView.data_position_diarios + 1) + "].selected = true; document.forms['frmConsultar'].submit();";

                    webView.loadUrl(URL + PG_DIARIOS);

                    loginFragment.textView_loading.setText(String.format(
                            getResources().getString(R.string.text_loading_first_login),
                            Integer.toString(i + 1), Integer.toString(webView.infos.data_diarios.length)));

                    return;
                }
            }
        }

        webView.data_position_diarios = 0;

        SharedPreferences.Editor editor = loginFragment.login_info.edit();
        editor.putBoolean(Utils.LOGIN_VALID, true);
        editor.putBoolean(Utils.FIRST_LOGIN, false);
        editor.apply();
        finish();
    }

    @Override
    public void onBackPressed(){
        this.moveTaskToBack(true);
    }
}
