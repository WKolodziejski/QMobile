package com.tinf.qmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tinf.qmobile.App;
import com.tinf.qmobile.fragment.LoginFragment;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.R;
import com.tinf.qmobile.utility.User;

public class LoginActivity extends AppCompatActivity implements OnResponse {
    private static String TAG = "LoginActivity";
    Fragment fragment;
    Snackbar snackBar;
    int pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState != null) {
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "loginFragment");
        } else {
            fragment = new LoginFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_fragment, fragment)
                .commit();
    }

    public void showSnackBar(String message) { //Mostra a SnackBar
        ViewGroup loginLayout = (ViewGroup) findViewById(R.id.login_container);

        //snackBar.setActionTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLight)));

        //View view = snackBar.getView();
        //TextView tv = (TextView) view.findViewById(R.id.snackbar_text);
        //tv.setTextColor(getResources().getColor(R.color.colorPrimaryLight));

        //  tv.setTextSize(getResources().getDimension(R.dimen.snackBar_font_size));
        //  A fonte fica bem pequena no meu celular, não sei se não era bom deixar um pouco maior.
        //  Mas aí fica por cima do botão entrar, tem q deixar mais espaço em branco embaixo.
        snackBar = Snackbar.make(loginLayout, message, Snackbar.LENGTH_INDEFINITE);
        snackBar.show();
    }

    public void dismissSnackbar() { //Esconde a SnackBar
        if (null != snackBar) {
            snackBar.dismiss();
            snackBar = null;
        }
    }

    @Override
    public void onFinish(int pg, int pos) {
        if (pg == PG_LOGIN) {
            Client.get().load(PG_FETCH_YEARS);
        }

        if (pg == PG_FETCH_YEARS) {
            for (int i = 0; i < User.getYears().length; i++) {
                Client.get().load(PG_DIARIOS, i);
            }
        }

        if (pg == PG_DIARIOS) {
            Client.get().load(PG_BOLETIM, pos);
            Client.get().load(PG_HORARIO, pos);
            if (pos == 0) {
                Client.get().load(PG_CALENDARIO, pos);
            }

            pages++;
        }

        if (pg == PG_BOLETIM) {
            pages++;
        }

        if (pg == PG_HORARIO) {
            pages++;
        }

        if (pg == PG_CALENDARIO) {
            pages++;
        }

        if (pages == User.getYears().length * 3 + 1) {
            User.setValid(true);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        Log.v(TAG, "Finished loading");
    }

    @Override
    public void onError(int pg, String error) {
        pages = 0;
        ((App) getApplication()).closeBoxStore();

        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

        Log.e(TAG, error);
    }

    @Override
    public void onAccessDenied(int pg, String message) {

        pages = 0;

        if (pg == PG_LOGIN) {
            showSnackBar(getResources().getString(R.string.login_invalid));

        } else if (pg == PG_ACESSO_NEGADO) {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_access_denied))
                    .setMessage(message)
                    .setCancelable(true)
                    .create()
                    .show();

        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.client_error), Toast.LENGTH_LONG).show();
            Log.wtf(TAG, "Somehow this come to happen");
        }

        Log.v(TAG, "Access denied");
    }

    @Override
    public void onStart(int pg, int pos) {
        Log.v(TAG, "Started loading");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
        Client.get().addOnResponseListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        Client.get().addOnResponseListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        //Client.get().removeOnResponseListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
        //Client.get().removeOnResponseListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        Client.get().removeOnResponseListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pages", pages);
        getSupportFragmentManager().putFragment(outState, "loginFragment", fragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pages = savedInstanceState.getInt("pages");
    }
}
