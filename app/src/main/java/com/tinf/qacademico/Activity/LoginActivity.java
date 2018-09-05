package com.tinf.qacademico.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tinf.qacademico.Fragment.LoginFragment;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Utils;
import com.tinf.qacademico.WebView.SingletonWebView;
import java.util.List;
import static com.tinf.qacademico.Utilities.Utils.PG_BOLETIM;
import static com.tinf.qacademico.Utilities.Utils.PG_CALENDARIO;
import static com.tinf.qacademico.Utilities.Utils.PG_DIARIOS;
import static com.tinf.qacademico.Utilities.Utils.PG_ERRO;
import static com.tinf.qacademico.Utilities.Utils.PG_HOME;
import static com.tinf.qacademico.Utilities.Utils.PG_HORARIO;
import static com.tinf.qacademico.Utilities.Utils.PG_LOGIN;
import static com.tinf.qacademico.Utilities.Utils.URL;

public class LoginActivity extends AppCompatActivity implements SingletonWebView.OnPageFinished, SingletonWebView.OnRecivedError {
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
        webView.setOnErrorRecivedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment, loginFragment).commit();
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

            } else if (url_p.contains(URL + PG_HOME)
                    || url_p.contains(URL + PG_BOLETIM)
                    || url_p.contains(URL + PG_HORARIO)
                    || url_p.contains(URL + PG_DIARIOS)
                    || url_p.contains(URL + PG_CALENDARIO)) {
                firstLogin();
            }
        });
    }

    public void showSnackBar(String message, boolean action) { //Mostra a SnackBar
        snackBar = Snackbar.make(loginLayout, message, Snackbar.LENGTH_INDEFINITE);
        //snackBar.setActionTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLight)));
        View view = snackBar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.colorPrimaryLight));

        //  tv.setTextSize(getResources().getDimension(R.dimen.snackBar_font_size));
        //  A fonte fica bem pequena no meu celular, não sei se não era bom deixar um pouco maior.
        //  Mas aí fica por cima do botão entrar, tem q deixar mais espaço em branco embaixo.


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

        if (!webView.pg_diarios_loaded[0]) {
            webView.loadUrl(URL + PG_DIARIOS);
            Log.i("LoginActivity", "DIARIOS[0]");

            loginFragment.textView_loading.setText(String.format(
                    getResources().getString(R.string.text_loading_first_login),
                    Integer.toString(1), "?"));

            return;
        } else {
            for (int i = 1; i < webView.infos.data_year.length; i++) {
                if (!webView.pg_diarios_loaded[i]) {
                    Log.i("LoginActivity", "DIARIOS[" + i + "]");
                    webView.year_position = i;

                    webView.scriptDiario = "javascript: var option = document.getElementsByTagName('option'); option["
                            + (webView.year_position + 1) + "].selected = true; document.forms['frmConsultar'].submit();";

                    webView.loadUrl(URL + PG_DIARIOS);

                    loginFragment.textView_loading.setText(String.format(
                            getResources().getString(R.string.text_loading_first_login),
                            Integer.toString(i + 1), Integer.toString(webView.infos.data_year.length)));

                    return;
                }
            }
        }

        webView.year_position = 0;

        if (!webView.pg_boletim_loaded[0]) {
            webView.loadUrl(URL + PG_BOLETIM);
            Log.i("LoginActivity", "BOLETIM[0]");

            loginFragment.textView_loading.setText(String.format(
                    getResources().getString(R.string.text_loading_first_login),
                    Integer.toString(1), "?"));

            return;
        } else {
            for (int i = 1; i < webView.infos.data_year.length; i++) {
                if (!webView.pg_boletim_loaded[i]) {
                    Log.i("LoginActivity", "BOLETIM[" + i + "]");
                    webView.year_position = i;

                    webView.loadUrl(URL + PG_BOLETIM + "&COD_MATRICULA=-1&cmbanos="
                            + webView.infos.data_year[i] + "&cmbperiodos=1&Exibir=Exibir+Boletim");

                    loginFragment.textView_loading.setText(String.format(
                            getResources().getString(R.string.text_loading_first_login),
                            Integer.toString(i + 1), Integer.toString(webView.infos.data_year.length)));

                    return;
                }
            }
        }

        webView.year_position = 0;

        if (!webView.pg_horario_loaded[0]) {
            webView.loadUrl(URL + PG_HORARIO);
            Log.i("LoginActivity", "HORARIO[0]");

            loginFragment.textView_loading.setText(String.format(
                    getResources().getString(R.string.text_loading_first_login),
                    Integer.toString(1), "?"));

            return;
        } else {
            for (int i = 1; i < webView.infos.data_year.length; i++) {
                if (!webView.pg_horario_loaded[i]) {
                    Log.i("LoginActivity", "HORARIO[" + i + "]");
                    webView.year_position = i;

                    webView.loadUrl(URL + PG_HORARIO + "&COD_MATRICULA=-1&cmbanos=" +
                            webView.infos.data_year[i] + "&cmbperiodos=1&Exibir=OK");

                    loginFragment.textView_loading.setText(String.format(
                            getResources().getString(R.string.text_loading_first_login),
                            Integer.toString(i + 1), Integer.toString(webView.infos.data_year.length)));

                    return;
                }
            }
        }

        webView.year_position = 0;

        if (!webView.pg_calendario_loaded) {
            webView.loadUrl(URL + PG_CALENDARIO);
            Log.i("LoginActivity", "CALENDÁRIO");

            loginFragment.textView_loading.setText(String.format(
                    getResources().getString(R.string.text_loading_first_login),
                    Integer.toString(1), Integer.toString(1)));

            return;
        }

        SharedPreferences.Editor editor = loginFragment.login_info.edit();
        editor.putBoolean(Utils.LOGIN_VALID, true);
        editor.putBoolean(Utils.FIRST_LOGIN, false);
        editor.apply();
        finish();
    }

    @Override
    public void onErrorRecived(String error) {
        loginFragment.textView_loading.setVisibility(View.INVISIBLE);
        loginFragment.dismissProgressBar();
    }

    @Override
    public void onBackPressed(){
        this.moveTaskToBack(true);
    }

}
