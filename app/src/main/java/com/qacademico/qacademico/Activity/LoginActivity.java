package com.qacademico.qacademico.Activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.qacademico.qacademico.Fragment.LoginFragment;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.WebView.SingletonWebView;

import static com.qacademico.qacademico.Utilities.Utils.pg_erro;
import static com.qacademico.qacademico.Utilities.Utils.pg_login;
import static com.qacademico.qacademico.Utilities.Utils.url;

public class LoginActivity extends AppCompatActivity implements SingletonWebView.OnPageFinished {
    public SingletonWebView mainWebView = SingletonWebView.getInstance();
    LoginFragment loginFragment = new LoginFragment();
    public Snackbar snackBar;
    ViewGroup loginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginLayout = (ViewGroup) findViewById(R.id.login_container);

        mainWebView.isLoginPage = true;
        mainWebView.setOnPageFinishedListener(this);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.login_fragment, loginFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onPageFinish(String url_p) {
        if (url_p.equals(url + pg_login)) {
            finish();
        } else if (url_p.equals(url + pg_erro)) {
            loginFragment.dismissProgressBar();
            showSnackBar(getResources().getString(R.string.text_invalid_login), false);
        }
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

    @Override
    public void onBackPressed(){
        this.moveTaskToBack(true);
    }
}
