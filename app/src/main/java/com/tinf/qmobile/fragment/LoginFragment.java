package com.tinf.qmobile.fragment;

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
import com.tinf.qmobile.databinding.FragmentLoginBinding;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.utility.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tinf.qmobile.utility.User.PASSWORD;
import static com.tinf.qmobile.utility.User.REGISTRATION;

public class LoginFragment extends Fragment implements OnResponse {
    private static final String TAG = "LoginFragment";
    private FragmentLoginBinding binding;
    private FirebaseRemoteConfig remoteConfig;
    private Map<String, String> urls;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        remoteConfig = FirebaseRemoteConfig.getInstance();

        remoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings
                .Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build());
        remoteConfig.setDefaultsAsync(R.xml.urls_map);
        urls = new Gson().fromJson(remoteConfig.getString("urls"),
                new TypeToken<Map<String, String>>(){}.getType());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        binding = FragmentLoginBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        if (urls != null)
            adapter.addAll(urls.keySet());
        else {
            List<String> urls = new ArrayList<>();
            urls.add("IFCE");
            urls.add("IFES");
            urls.add("IFG");
            urls.add("IFGOIANO");
            urls.add("IFMA");
            urls.add("IFMT");
            urls.add("IFPE");
            urls.add("IFPI");
            urls.add("IFRS");
            adapter.addAll(urls);
        }

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        urls = new Gson().fromJson(remoteConfig.getString("urls"),
                                new TypeToken<Map<String, String>>(){}.getType());

                        if (urls != null) {
                            adapter.clear();
                            adapter.addAll(urls.keySet());
                            binding.spinner.setAdapter(adapter);
                            binding.spinner.setSelection(0);
                        }
                    }
                });

        binding.spinner.setAdapter(adapter);
        binding.spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        if (urls != null)
                            Client.get().setURL(urls.get(adapter.getItem(position)));

                        Log.d(TAG, Client.get().getURL());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {}

                });
        binding.spinner.setSelection(0);

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

                User.setCredential(REGISTRATION, binding.userInput.getText().toString().toUpperCase().trim());
                User.setCredential(PASSWORD, binding.passwordInput.getText().toString().trim());

                if (urls == null)
                    Client.get().setURL(Arrays.asList(getResources().getStringArray(R.array.urls)).get(binding.spinner.getSelectedItemPosition()));

                FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
                crashlytics.setCustomKey("Register", User.getCredential(REGISTRATION));
                crashlytics.setCustomKey("URL", User.getURL());

                Client.get().login();
            }
        });

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
    public void onFinish(int pg) {
        Log.v(TAG, "Finished loading");
    }

    @Override
    public void onError(int pg, String error) {
        binding.progressBar.setVisibility(GONE);
        binding.textLoading.setVisibility(View.GONE);
        binding.btn.setEnabled(true);
        Log.e(TAG, error);
    }

    @Override
    public void onAccessDenied(int pg, String message) {
        binding.progressBar.setVisibility(GONE);
        binding.textLoading.setVisibility(View.GONE);
        binding.btn.setEnabled(true);
        binding.userInput.setError("");
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
