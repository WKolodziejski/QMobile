package com.tinf.qacademico.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tinf.qacademico.Activity.LoginActivity;
import com.tinf.qacademico.R;
import com.tinf.qacademico.Utilities.Utils;

import java.util.Objects;

import static com.tinf.qacademico.Utilities.Utils.PG_LOGIN;
import static com.tinf.qacademico.Utilities.Utils.URL;

public class LoginFragment extends Fragment {
    public SharedPreferences login_info;
    ProgressBar progressBar_login;
    public TextView textView_loading;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        login_info = Objects.requireNonNull(getActivity()).getSharedPreferences(Utils.LOGIN_INFO, 0);

        EditText user_et = (TextInputEditText) view.findViewById(R.id.user_input_login);
        EditText password_et = (TextInputEditText) view.findViewById(R.id.password_input_login);
        progressBar_login = (ProgressBar) view.findViewById(R.id.login_progressbar);
        textView_loading = (TextView) view.findViewById(R.id.login_textLoading);
        Button login_btn = (Button) view.findViewById(R.id.btn_login);

        login_btn.setOnClickListener(v -> {
            View vi = getActivity().getCurrentFocus();
            if (vi != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(vi.getWindowToken(), 0);
                }
            }

            progressBar_login.setVisibility(View.VISIBLE);

            SharedPreferences.Editor editor = login_info.edit();
            editor.putString(Utils.LOGIN_REGISTRATION, user_et.getText().toString().toUpperCase());
            editor.putString(Utils.LOGIN_PASSWORD, password_et.getText().toString());
            editor.apply();

            ((LoginActivity) getActivity()).dismissSnackbar();
            ((LoginActivity) getActivity()).webView.loadUrl(URL + PG_LOGIN);
        });
        return view;
    }

    public void dismissProgressBar(){
        progressBar_login.setVisibility(View.GONE);
        textView_loading.setVisibility(View.VISIBLE);
    }
}
