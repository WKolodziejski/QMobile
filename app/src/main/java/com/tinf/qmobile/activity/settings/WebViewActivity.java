package com.tinf.qmobile.activity.settings;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tinf.qmobile.R;

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
