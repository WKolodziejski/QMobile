package com.tinf.qmobile.Fragment;

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
import com.tinf.qmobile.Activity.LoginActivity;
import com.tinf.qmobile.R;
import com.tinf.qmobile.Utilities.User;
import com.tinf.qmobile.WebView.SingletonWebView;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tinf.qmobile.Utilities.User.PASSWORD;
import static com.tinf.qmobile.Utilities.User.REGISTRATION;
import static com.tinf.qmobile.Utilities.Utils.PG_LOGIN;
import static com.tinf.qmobile.Utilities.Utils.URL;

public class LoginFragment extends Fragment {
    private ProgressBar progressBar_login;
    public TextView textView_loading;
    public Button login_btn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText user_et = (TextInputEditText) view.findViewById(R.id.user_input_login);
        EditText password_et = (TextInputEditText) view.findViewById(R.id.password_input_login);
        progressBar_login = (ProgressBar) view.findViewById(R.id.login_progressbar);
        textView_loading = (TextView) view.findViewById(R.id.login_textLoading);
        login_btn = (Button) view.findViewById(R.id.btn_login);

        login_btn.setOnClickListener(v -> {
            View vi = getActivity().getCurrentFocus();
            if (vi != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(vi.getWindowToken(), 0);
                }
            }

            progressBar_login.setVisibility(VISIBLE);
            login_btn.setClickable(false);

            User.setCredential(getContext(), REGISTRATION, user_et.getText().toString().toUpperCase());
            User.setCredential(getContext(), PASSWORD, password_et.getText().toString());

            ((LoginActivity) getActivity()).dismissSnackbar();
            SingletonWebView.getInstance().loadUrl(URL + PG_LOGIN);
        });
    }

    public void dismissProgressBar(){
        progressBar_login.setVisibility(GONE);
        textView_loading.setVisibility(VISIBLE);
    }
}
