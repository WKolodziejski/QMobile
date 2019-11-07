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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tinf.qmobile.R;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.utility.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tinf.qmobile.utility.User.PASSWORD;
import static com.tinf.qmobile.utility.User.REGISTRATION;
import static com.tinf.qmobile.utility.User.getYear;

public class LoginFragment extends Fragment implements OnResponse {
    private static String TAG = "LoginFragment";
    @BindView(R.id.login_progressbar)       ProgressBar progressBar;
    @BindView(R.id.login_textLoading)       TextView textView;
    @BindView(R.id.user_input_login)        EditText user;
    @BindView(R.id.password_input_login)    EditText password;
    @BindView(R.id.btn_login)               Button btn;
    @BindView(R.id.login_spinner)           Spinner spinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<String> urls = new ArrayList<>();
        urls.add("IFSUL");
        urls.add("IFES");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        adapter.addAll(urls);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        switch (position) {
                            case 0 : Client.get().setURL(IFSUL);
                                break;

                            case 1: Client.get().setURL(IFES);
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {}

                });

        btn.setOnClickListener(v -> {

            if (user.getText().toString().isEmpty()) {
                user.setError(getResources().getString(R.string.text_empty));
            }

            if (password.getText().toString().isEmpty()) {
                password.setError(getResources().getString(R.string.text_empty));
            }

            if (!user.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {

                hideKeyboard();
                user.setError(null);

                User.setCredential(REGISTRATION, user.getText().toString().toUpperCase());
                User.setCredential(PASSWORD, password.getText().toString());

                Client.get().login();
            }
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
    public void onStart(int pg, int pos) {
        progressBar.setVisibility(VISIBLE);
        textView.setVisibility(View.VISIBLE);
        btn.setClickable(false);

        if (pg == PG_GERADOR) {
            textView.setText(getResources().getString(R.string.login_token));

        } else if (pg == PG_LOGIN) {
            textView.setText(getResources().getString(R.string.login_validating));

        } else if (pg ==  PG_FETCH_YEARS) {
            textView.setText(getResources().getString(R.string.login_checking));

        } else {
            textView.setText(String.format(
                    getResources().getString(R.string.login_loading),
                    String.valueOf(getYear(pos))));
        }

        Log.v(TAG, "Started loading");
    }

    @OnClick(R.id.login_help)
    public void openHelp(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://sites.google.com/view/qmobileapp/ajuda"));
        startActivity(browserIntent);
    }

    @Override
    public void onFinish(int pg, int pos) {
        Log.v(TAG, "Finished loading");
    }

    @Override
    public void onError(int pg, String error) {
        progressBar.setVisibility(GONE);
        textView.setVisibility(View.GONE);
        btn.setClickable(true);
        Log.e(TAG, error);
    }

    @Override
    public void onAccessDenied(int pg, String message) {
        progressBar.setVisibility(GONE);
        textView.setVisibility(View.GONE);
        btn.setClickable(true);
        user.setError("");
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("user", user.getText().toString());
        outState.putString("pass", password.getText().toString());
        outState.putInt("pgr", progressBar.getVisibility());
        outState.putBoolean("btn", btn.isClickable());
        outState.putCharSequence("txt_txt", textView.getText());
        outState.putInt("txt_vis", textView.getVisibility());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            user.setText(savedInstanceState.getString("user"));
            password.setText(savedInstanceState.getString("pass"));
            progressBar.setVisibility(savedInstanceState.getInt("pgr"));
            btn.setClickable(savedInstanceState.getBoolean("btn"));
            textView.setText(savedInstanceState.getCharSequence("txt_txt"));
            textView.setVisibility(savedInstanceState.getInt("txt_vis"));
        }
    }

}
