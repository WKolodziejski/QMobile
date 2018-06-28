package com.qacademico.qacademico.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.qacademico.qacademico.Fragment.MateriaisFragment;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;

public class MateriaisActivity extends AppCompatActivity implements SingletonWebView.OnPageFinished {
    SingletonWebView webView = SingletonWebView.getInstance();
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_materiais);

        webView.setOnPageFinishedListener(this);

        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_materiais);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment, new MateriaisFragment(), Utils.MATERIAIS).commit();
    }

    @Override
    public void onPageFinish(String url_p, List<?> list) {

    }
}
