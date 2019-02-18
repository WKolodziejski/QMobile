package com.tinf.qmobile.Activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.tinf.qmobile.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            WebView webView = (WebView) findViewById(R.id.webview);
            webView.loadUrl(extras.getString("URL"));

        } else {
            finish();
        }
    }
}
