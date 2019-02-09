package com.tinf.qmobile.Activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Fragment.LoginFragment;
import com.tinf.qmobile.Interfaces.OnResponse;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.User;
import com.tinf.qmobile.Utilities.Utils;

import static com.tinf.qmobile.Network.Client.PG_ACESSO_NEGADO;
import static com.tinf.qmobile.Network.Client.PG_BOLETIM;
import static com.tinf.qmobile.Network.Client.PG_CALENDARIO;
import static com.tinf.qmobile.Network.Client.PG_DIARIOS;
import static com.tinf.qmobile.Network.Client.PG_HORARIO;
import static com.tinf.qmobile.Network.Client.PG_LOGIN;

public class LoginActivity extends AppCompatActivity implements OnResponse {
    private static String TAG = "LoginActivity";
    OnResponse onResponse;
    Snackbar snackBar;
    int pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_fragment, new LoginFragment())
                .commit();
    }

    public void showSnackBar(String message) { //Mostra a SnackBar
        ViewGroup loginLayout = (ViewGroup) findViewById(R.id.login_container);
        snackBar = Snackbar.make(loginLayout, message, Snackbar.LENGTH_INDEFINITE);
        snackBar.setActionTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLight)));

        View view = snackBar.getView();
        TextView tv = (TextView) view.findViewById(R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.colorPrimaryLight));

        //  tv.setTextSize(getResources().getDimension(R.dimen.snackBar_font_size));
        //  A fonte fica bem pequena no meu celular, não sei se não era bom deixar um pouco maior.
        //  Mas aí fica por cima do botão entrar, tem q deixar mais espaço em branco embaixo.
        snackBar.show();
    }

    public void dismissSnackbar() { //Esconde a SnackBar
        if (null != snackBar) {
            snackBar.dismiss();
            snackBar = null;
        }
    }

    @Override
    public void onFinish(int pg, int year) {
        if (pg == PG_LOGIN) {
            ((App) getApplication()).setLogged(true);
            Client.get().load(PG_DIARIOS);
        }

        if (pg == PG_DIARIOS) {

            if (year == 0) {
                for (int i = 1; i < User.getYears().length; i++) {
                    Client.get().load(PG_DIARIOS, i);
                }
            }

            Client.get().load(PG_BOLETIM, year);
            Client.get().load(PG_HORARIO, year);
            Client.get().load(PG_CALENDARIO, year);

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
            finish();
        }

        onResponse.onFinish(pg, year);

        Log.v(TAG, "Finished loading");
    }

    @Override
    public void onError(int pg, String error) {
        ((App) getApplication()).setLogged(false);

        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

        onResponse.onError(pg, error);

        Log.e(TAG, error);
    }

    @Override
    public void onAccessDenied(int pg, String message) {

        if (pg == PG_LOGIN) {
            showSnackBar(getResources().getString(R.string.text_invalid_login));

        } else if (pg == PG_ACESSO_NEGADO) {
            new android.app.AlertDialog.Builder(LoginActivity.this)
                    .setCustomTitle(Utils.customAlertTitle(this, R.drawable.ic_error_black_24dp, R.string.dialog_access_denied, R.color.error))
                    .setMessage(message)
                    .setCancelable(true)
                    .create()
                    .show();
        } else {
            //TODO mensagem de acesso negado bizarro
            Toast.makeText(getApplicationContext(), "Ocorreu algo muito errado", Toast.LENGTH_LONG).show();
        }

        onResponse.onAccessDenied(pg, message);

        Log.v(TAG, "Access denied");
    }

    @Override
    public void onBackPressed(){
        this.moveTaskToBack(true);
    }

    @Override
    public void onStart(String url, int year) {
        onResponse.onStart(url, year);

        Log.v(TAG, "Started loading");
    }

    @Override
    public void onStart() {
        super.onStart();
        Client.get().setOnResponseListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Client.get().setOnResponseListener(this);
    }

    public void setOnResponsListener(OnResponse onResponse) {
        this.onResponse = onResponse;
    }

}
