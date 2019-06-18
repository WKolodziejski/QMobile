package com.tinf.qmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
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

    @Override
    public void onFinish(int pg, int pos) {
        /*  DEBUG VERSION

        if (pg == PG_LOGIN) {
            Client.get().load(PG_FETCH_YEARS);
        }

        if (pg == PG_FETCH_YEARS) {
            Client.get().load(PG_DIARIOS, 0);
        }

        if (pg == PG_DIARIOS) {
            Client.get().load(PG_BOLETIM, pos);
            pages++;
        }

        if (pg == PG_BOLETIM) {
            Client.get().load(PG_HORARIO, pos);
            pages++;
        }

        if (pg == PG_HORARIO) {
            pages++;

            if (pos < User.getYears().length - 1) {
                Client.get().load(PG_DIARIOS, pos + 1);
            } else {
                Client.get().load(PG_CALENDARIO, pos);
            }
        }

        if (pg == PG_CALENDARIO) {
            pages++;
        }*/

        // =================================================


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

        // =================================================

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
        App.closeBoxStore();

        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

        Log.e(TAG, error);
    }

    @Override
    public void onAccessDenied(int pg, String message) {

        pages = 0;

       if (pg == PG_ACESSO_NEGADO) {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_access_denied))
                    .setMessage(message)
                    .setCancelable(true)
                    .create()
                    .show();

       } else {
           App.closeBoxStore();
           Toast.makeText(getApplicationContext(), getResources().getString(R.string.client_error), Toast.LENGTH_LONG).show();
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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
