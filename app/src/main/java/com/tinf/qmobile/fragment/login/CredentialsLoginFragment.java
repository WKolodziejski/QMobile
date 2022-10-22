package com.tinf.qmobile.fragment.login;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tinf.qmobile.utility.UserUtils.PASSWORD;
import static com.tinf.qmobile.utility.UserUtils.REGISTRATION;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.FragmentLoginCredentialsBinding;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.utility.UserUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CredentialsLoginFragment extends Fragment implements OnResponse {
    private static final String TAG = "CredentialLoginFragment";
    private FragmentLoginCredentialsBinding binding;
    private int i;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            i = getArguments().getInt("I");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_credentials, container, false);
        binding = FragmentLoginCredentialsBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btn.setOnClickListener(v -> {

            if (binding.userInput.getText().toString().isEmpty()) {
                binding.userInput.setError(getResources().getString(R.string.text_empty));
            }

            if (binding.passwordInput.getText().toString().isEmpty()) {
                binding.passwordInput.setError(getResources().getString(R.string.text_empty));
            }

            if (!binding.userInput.getText().toString().isEmpty() && !binding.passwordInput.getText().toString().isEmpty()) {
                hideKeyboard();
                binding.userInput.setError(null);

                UserUtils.setCredential(REGISTRATION, binding.userInput.getText().toString().toUpperCase().trim());
                UserUtils.setCredential(PASSWORD, binding.passwordInput.getText().toString().trim());

                if (UserUtils.getURL().isEmpty())
                    Client.get().setURL(Arrays.asList(getResources().getStringArray(R.array.urls)).get(i));

                FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
                crashlytics.setCustomKey("Register", UserUtils.getCredential(REGISTRATION));
                crashlytics.setCustomKey("Password", UserUtils.getCredential(PASSWORD));
                crashlytics.setCustomKey("URL", UserUtils.getURL());

                Client.get().login();
            }
        });

        binding.keepLogin.setOnClickListener(view1 -> UserUtils.setKeep(binding.keepLogin.isChecked()));

        binding.help.setOnClickListener(view1 -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://sites.google.com/view/qmobileapp/ajuda"));
            startActivity(browserIntent);
        });
    }

    private void hideKeyboard() {
        View vi = getActivity().getCurrentFocus();
        if (vi != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(vi.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onStart(int pg) {
        binding.progressBar.setVisibility(VISIBLE);
        binding.textLoading.setVisibility(View.VISIBLE);
        binding.btn.setEnabled(false);

        if (pg == PG_GENERATOR) {
            binding.textLoading.setText(getResources().getString(R.string.login_token));

        } else if (pg == PG_LOGIN) {
            binding.textLoading.setText(getResources().getString(R.string.login_validating));

        } else if (pg == PG_FETCH_YEARS) {
            binding.textLoading.setText(getResources().getString(R.string.login_checking));

        } else {
            binding.textLoading.setText(getResources().getString(R.string.login_loading));
        }

        Log.v(TAG, "Started loading");
    }

    @Override
    public void onFinish(int pg, int year, int period) {
        Log.v(TAG, "Finished loading");
    }

    @Override
    public void onError(int pg, String error) {
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.textLoading.setVisibility(View.INVISIBLE);
        binding.btn.setEnabled(true);
        Log.e(TAG, error);

        try {
            int pid = android.os.Process.myPid();
            String command = "logcat --pid=" + pid + " -d";
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                log.append(line + "\n");
            }

            FirebaseCrashlytics.getInstance().log(log.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccessDenied(int pg, String message) {
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.textLoading.setVisibility(View.INVISIBLE);
        binding.btn.setEnabled(true);
        binding.userInput.setError(message);
        Log.v(TAG, "Access denied");
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
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        Client.get().removeOnResponseListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
        Client.get().removeOnResponseListener(this);
    }

}
