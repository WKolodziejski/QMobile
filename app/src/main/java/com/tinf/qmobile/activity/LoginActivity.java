package com.tinf.qmobile.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.ActivityLoginBinding;
import com.tinf.qmobile.fragment.login.WelcomeLoginFragment;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.service.FirebaseMessageParams;
import com.tinf.qmobile.utility.Design;
import com.tinf.qmobile.utility.UserUtils;

import java.util.Date;

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
            fragment = new WelcomeLoginFragment();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.login_fragment, fragment)
                .commit();

        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseMessageParams params = null;

        try {
            params = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm").
                    create()
                    .fromJson(
                            remoteConfig.getString("message_login"),
                            new TypeToken<FirebaseMessageParams>() {
                            }.getType());
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        if (params == null)
            return;

        if (params.showAfter == null || params.hideAfter == null || params.message == null || params.show == null)
            return;

        Date now = new Date();

        if (!params.show || params.showAfter.after(now) || params.hideAfter.before(now)) {
            binding.warningCard.setVisibility(View.GONE);
            return;
        }

        binding.warningCard.setCardBackgroundColor(Design.getColorForWarning(getBaseContext(), params.color));
        binding.warningText.setText(params.message);
        binding.warningCard.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinish(int pg, int year, int period) {
        if (pg == PG_LOGIN) {
            Client.get().load(PG_FETCH_YEARS);

        } else if (pg == PG_FETCH_YEARS) {
            UserUtils.setValid(true);
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

        } else if (pg == PG_UPDATE) {
            new MaterialAlertDialogBuilder(LoginActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_update_password))
                    .setMessage(getResources().getString(R.string.dialog_update_password_msg))
                    .setCancelable(true)
                    .create()
                    .show();

        } else if (pg == PG_QUEST) {
            new MaterialAlertDialogBuilder(LoginActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_questionary_title))
                    .setMessage(getResources().getString(R.string.dialog_questionary_text))
                    .setCancelable(true)
                    .setPositiveButton(getResources().getString(R.string.dialog_open_site),
                            (dialogInterface, i) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(UserUtils.getURL()))))
                    .create()
                    .show();

        } else if (pg == PG_REGISTRATION) {
            new MaterialAlertDialogBuilder(LoginActivity.this)
                    .setTitle(getResources().getString(R.string.dialog_registration_title))
                    .setMessage(getResources().getString(R.string.dialog_registration_text))
                    .setCancelable(true)
                    .setPositiveButton(getResources().getString(R.string.dialog_open_site),
                            (dialogInterface, i) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(UserUtils.getURL()))))
                    .create()
                    .show();

        } else {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.dialog_access_denied), Toast.LENGTH_LONG).show();
        }

        Log.v(TAG, message);
    }

    @Override
    public void onStart(int pg) {
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
