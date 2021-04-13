package com.tinf.qmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.ActivityLoginBinding;
import com.tinf.qmobile.fragment.LoginFragment;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.utility.User;

import static com.tinf.qmobile.fragment.SettingsFragment.NIGHT;

public class LoginActivity extends AppCompatActivity implements OnResponse {
    private ActivityLoginBinding binding;
    private static final String TAG = "LoginActivity";
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

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
        if (pg == PG_LOGIN) {
            Client.get().load(PG_FETCH_YEARS);
        } else if (pg == PG_JOURNALS) {
            Client.get().load(PG_CALENDAR);
            User.setValid(true);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onError(int pg, String error) {
        Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();

        Log.e(TAG, error);
    }

    @Override
    public void onAccessDenied(int pg, String message) {
       if (pg == PG_ACCESS_DENIED) {
            new MaterialAlertDialogBuilder(LoginActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_access_denied))
                    .setMessage(message)
                    .setCancelable(true)
                    .create()
                    .show();

       } else {
           Toast.makeText(getBaseContext(), getResources().getString(R.string.dialog_access_denied), Toast.LENGTH_LONG).show();
       }

       Log.v(TAG, message);
    }

    @Override
    public void onStart(int pg, int pos) {
        Log.v(TAG, "Started loading " + pg);
    }

    @Override
    public void onStart() {
        super.onStart();
        Client.get().addOnResponseListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Client.get().addOnResponseListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Client.get().removeOnResponseListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Client.get().removeOnResponseListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Client.get().removeOnResponseListener(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "loginFragment", fragment);
    }

}
