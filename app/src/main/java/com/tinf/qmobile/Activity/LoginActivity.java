package com.tinf.qmobile.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tinf.qmobile.App;
import com.tinf.qmobile.Fragment.LoginFragment;
import com.tinf.qmobile.Interfaces.WebView.OnPageLoad;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.Utils;
import com.tinf.qmobile.WebView.SingletonWebView;

import static com.tinf.qmobile.Utilities.Utils.PG_BOLETIM;
import static com.tinf.qmobile.Utilities.Utils.PG_CALENDARIO;
import static com.tinf.qmobile.Utilities.Utils.PG_DIARIOS;
import static com.tinf.qmobile.Utilities.Utils.PG_ERRO;
import static com.tinf.qmobile.Utilities.Utils.PG_HOME;
import static com.tinf.qmobile.Utilities.Utils.PG_HORARIO;
import static com.tinf.qmobile.Utilities.Utils.PG_LOGIN;
import static com.tinf.qmobile.Utilities.Utils.URL;

public class LoginActivity extends AppCompatActivity implements OnPageLoad.Main {
    private SingletonWebView webView = SingletonWebView.getInstance();
    LoginFragment loginFragment = new LoginFragment();
    public Snackbar snackBar;
    ViewGroup loginLayout;
    int num = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginLayout = (ViewGroup) findViewById(R.id.login_container);

        webView.isLoginPage = true;

        getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment, loginFragment).commit();
    }

    public void showSnackBar(String message, boolean action) { //Mostra a SnackBar
        snackBar = Snackbar.make(loginLayout, message, Snackbar.LENGTH_INDEFINITE);
        //snackBar.setActionTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLight)));

        /*View view = snackBar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.colorPrimaryLight));*/

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

    private void loadData() {
        Log.i("LoginActivity", "FirstLogin()");

        loginFragment.textView_loading.setVisibility(View.VISIBLE);

        if (!webView.pg_diarios_loaded[0]) {
            webView.loadUrl(URL + PG_DIARIOS);
            Log.i("LoginActivity", "DIARIOS[0]");

            loginFragment.textView_loading.setText(String.format(
                    getResources().getString(R.string.text_loading_first_login),
                    Integer.toString(num), Integer.toString(webView.data_year.length * 3 + 1)));

            return;
        } else {
            for (int i = 1; i < webView.data_year.length; i++) {
                if (!webView.pg_diarios_loaded[i]) {
                    Log.i("LoginActivity", "DIARIOS[" + i + "]");
                    webView.year_position = i;

                    webView.scriptDiario = "javascript: var option = document.getElementsByTagName('option'); option["
                            + (webView.year_position + 1) + "].selected = true; document.forms['frmConsultar'].submit();";

                    webView.loadUrl(URL + PG_DIARIOS);

                    num++;

                    loginFragment.textView_loading.setText(String.format(
                            getResources().getString(R.string.text_loading_first_login),
                            Integer.toString(num), Integer.toString(webView.data_year.length * 3 + 1)));

                    return;
                }
            }
        }

        webView.year_position = 0;

        if (!webView.pg_boletim_loaded[0]) {
            webView.loadUrl(URL + PG_BOLETIM);
            Log.i("LoginActivity", "BOLETIM[0]");

            num++;

            loginFragment.textView_loading.setText(String.format(
                    getResources().getString(R.string.text_loading_first_login),
                    Integer.toString(num), Integer.toString(webView.data_year.length * 3 + 1)));

            return;
        } else {
            for (int i = 1; i < webView.data_year.length; i++) {
                if (!webView.pg_boletim_loaded[i]) {
                    Log.i("LoginActivity", "BOLETIM[" + i + "]");
                    webView.year_position = i;

                    webView.loadUrl(URL + PG_BOLETIM + "&COD_MATRICULA=-1&cmbanos="
                            + webView.data_year[i] + "&cmbperiodos=1&Exibir=Exibir+Boletim");

                    num++;

                    loginFragment.textView_loading.setText(String.format(
                            getResources().getString(R.string.text_loading_first_login),
                            Integer.toString(num), Integer.toString(webView.data_year.length * 3 + 1)));

                    return;
                }
            }
        }

        webView.year_position = 0;

        if (!webView.pg_horario_loaded[0]) {
            webView.loadUrl(URL + PG_HORARIO);
            Log.i("LoginActivity", "HORARIO[0]");

            num++;

            loginFragment.textView_loading.setText(String.format(
                    getResources().getString(R.string.text_loading_first_login),
                    Integer.toString(num), Integer.toString(webView.data_year.length * 3 + 1)));

            return;
        } else {
            for (int i = 1; i < webView.data_year.length; i++) {
                if (!webView.pg_horario_loaded[i]) {
                    Log.i("LoginActivity", "HORARIO[" + i + "]");
                    webView.year_position = i;

                    webView.loadUrl(URL + PG_HORARIO + "&COD_MATRICULA=-1&cmbanos=" +
                            webView.data_year[i] + "&cmbperiodos=1&Exibir=OK");

                    num++;

                    loginFragment.textView_loading.setText(String.format(
                            getResources().getString(R.string.text_loading_first_login),
                            Integer.toString(num), Integer.toString(webView.data_year.length  * 3 + 1)));

                    return;
                }
            }
        }

        webView.year_position = 0;

        if (!webView.pg_calendario_loaded) {
            webView.loadUrl(URL + PG_CALENDARIO);
            Log.i("LoginActivity", "CALENDÁRIO");

            num++;

            loginFragment.textView_loading.setText(String.format(
                    getResources().getString(R.string.text_loading_first_login),
                    Integer.toString(num), Integer.toString(webView.data_year.length * 3 + 1)));

            return;
        }

        webView.isLoginPage = false;

        SharedPreferences.Editor editor = loginFragment.login_info.edit();
        editor.putBoolean(Utils.LOGIN_VALID, true);
        editor.apply();
        finish();
    }

    @Override
    public void onPageStart() {}

    @Override
    public void onPageFinish(String url_p) {
        runOnUiThread(() -> {
            if (url_p.equals(URL + PG_LOGIN)) {
                ((App) getApplication()).setLogged(true);
                webView.setBoxStore(((App) getApplication()).getBoxStore());

            } else if (url_p.equals(URL + PG_ERRO)) {
                loginFragment.dismissProgressBar();
                showSnackBar(getResources().getString(R.string.text_invalid_login), false);

            } else if (url_p.contains(URL + PG_HOME)
                    || url_p.contains(URL + PG_BOLETIM)
                    || url_p.contains(URL + PG_HORARIO)
                    || url_p.contains(URL + PG_DIARIOS)
                    || url_p.contains(URL + PG_CALENDARIO)) {
                loadData();
            }
        });
    }

    @Override
    public void onErrorRecived(String error) {
        runOnUiThread(() -> {
            loginFragment.textView_loading.setVisibility(View.INVISIBLE);
            loginFragment.dismissProgressBar();
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            num = 0;
            webView.year_position = 0;
        });
    }

    @Override
    public void onBackPressed(){
        this.moveTaskToBack(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        webView.setOnPageLoadListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.setOnPageLoadListener(this);
    }
}
