package com.tinf.qacademico.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.tinf.qacademico.WebView.SingletonWebView;

import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_INFO;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_PASSWORD;
import static com.tinf.qacademico.Utilities.Utils.LOGIN_REGISTRATION;
import static com.tinf.qacademico.Utilities.Utils.PG_LOGIN;
import static com.tinf.qacademico.Utilities.Utils.URL;

public class LoginFragment extends Fragment {
    public SharedPreferences login_info;
    private ProgressBar progressBar_login;
    public TextView textView_loading;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        login_info = Objects.requireNonNull(getActivity()).getSharedPreferences(LOGIN_INFO, MODE_PRIVATE);

        EditText user_et = (TextInputEditText) view.findViewById(R.id.user_input_login);
        EditText password_et = (TextInputEditText) view.findViewById(R.id.password_input_login);
        progressBar_login = (ProgressBar) view.findViewById(R.id.login_progressbar);
        textView_loading = (TextView) view.findViewById(R.id.login_textLoading);
        Button login_btn = (Button) view.findViewById(R.id.btn_login);

        login_btn.setOnClickListener(v -> {
            View vi = getActivity().getCurrentFocus();
            if (vi != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(vi.getWindowToken(), 0);
                }
            }

            progressBar_login.setVisibility(VISIBLE);

            SharedPreferences.Editor editor = login_info.edit();
            editor.putString(LOGIN_REGISTRATION, user_et.getText().toString().toUpperCase());
            editor.putString(LOGIN_PASSWORD, password_et.getText().toString());
            editor.apply();

            ((LoginActivity) getActivity()).dismissSnackbar();
            SingletonWebView.getInstance().loadUrl(URL + PG_LOGIN);
        });
    }

    public void dismissProgressBar(){
        progressBar_login.setVisibility(GONE);
        textView_loading.setVisibility(VISIBLE);
    }
}
