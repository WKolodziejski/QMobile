package com.qacademico.qacademico.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.qacademico.qacademico.Fragment.CalendarioFragment;
import com.qacademico.qacademico.Fragment.LoginFragment;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;

import java.util.List;

public class CalendarioActivity extends AppCompatActivity implements SingletonWebView.OnPageFinished {
    public SingletonWebView mainWebView = SingletonWebView.getInstance();
    CalendarioFragment calendarioFragment = new CalendarioFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        mainWebView.setOnPageFinishedListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.calendario_fragment, calendarioFragment, Utils.CALENDARIO);
        fragmentTransaction.commit();
    }

    @Override
    public void onPageFinish(String url_p, List<?> list) {

    }
}
