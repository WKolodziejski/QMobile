package com.qacademico.qacademico.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.qacademico.qacademico.Fragment.HorarioFragment;
import com.qacademico.qacademico.R;
import com.qacademico.qacademico.Utilities.Utils;
import com.qacademico.qacademico.WebView.SingletonWebView;

import java.util.List;
import java.util.Objects;

public class HorarioActivity extends AppCompatActivity implements SingletonWebView.OnPageFinished {
    public SingletonWebView mainWebView = SingletonWebView.getInstance();
    HorarioFragment horarioFragment = new HorarioFragment();
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_horario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainWebView.setOnPageFinishedListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.horario_fragment, horarioFragment, Utils.HORARIO);
        fragmentTransaction.commit();
    }

    @Override
    public void onPageFinish(String url_p, List<?> list) {

    }
}