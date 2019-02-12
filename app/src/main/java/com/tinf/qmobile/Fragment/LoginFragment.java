package com.tinf.qmobile.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.tinf.qmobile.Activity.LoginActivity;
import com.tinf.qmobile.Interfaces.OnResponse;
import com.tinf.qmobile.Network.Client;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.User;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tinf.qmobile.Network.Client.PG_BOLETIM;
import static com.tinf.qmobile.Network.Client.PG_CALENDARIO;
import static com.tinf.qmobile.Network.Client.PG_DIARIOS;
import static com.tinf.qmobile.Network.Client.PG_GERADOR;
import static com.tinf.qmobile.Network.Client.PG_HORARIO;
import static com.tinf.qmobile.Network.Client.PG_LOGIN;
import static com.tinf.qmobile.Utilities.User.PASSWORD;
import static com.tinf.qmobile.Utilities.User.REGISTRATION;
import static com.tinf.qmobile.Utilities.User.getYear;

public class LoginFragment extends Fragment implements OnResponse {
    private static String TAG = "LoginFragment";
    @BindView(R.id.login_progressbar)       ProgressBar progressBar;
    @BindView(R.id.login_textLoading)       TextView textView;
    @BindView(R.id.user_input_login)        EditText user;
    @BindView(R.id.password_input_login)    EditText password;
    @BindView(R.id.btn_login)               Button btn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn.setOnClickListener(v -> {

            if (user.getText().toString().isEmpty()) {
                user.setError(getResources().getString(R.string.text_empty));
            }

            if (password.getText().toString().isEmpty()) {
                password.setError(getResources().getString(R.string.text_empty));
            }

            if (!user.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {

                hideKeyboard();

                User.setCredential(REGISTRATION, user.getText().toString().toUpperCase());
                User.setCredential(PASSWORD, password.getText().toString());

                ((LoginActivity) getActivity()).dismissSnackbar();
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
    public void onStart(int pg, int year) {
        progressBar.setVisibility(VISIBLE);
        textView.setVisibility(View.VISIBLE);
        btn.setClickable(false);

        if (pg == PG_GERADOR) {
            textView.setText(getResources().getString(R.string.login_token));

        } else if (pg == PG_LOGIN) {
            textView.setText(getResources().getString(R.string.login_validating));

        } else if (pg ==  PG_DIARIOS) {
            if (year != 0) {
                textView.setText(String.format(
                        getResources().getString(R.string.login_loading),
                        String.valueOf(getYear(year))));

            } else {
                textView.setText(getResources().getString(R.string.login_checking));
            }

        }

        Log.v(TAG, "Started loading");
    }

    @Override
    public void onFinish(int pg, int year) {
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
        user.setError("Inválido");
        password.setError("Inválido");
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
