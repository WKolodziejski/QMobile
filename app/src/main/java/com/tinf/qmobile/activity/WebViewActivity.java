package com.tinf.qmobile.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tinf.qmobile.R;
import com.tinf.qmobile.databinding.ActivityWebviewBinding;
import com.tinf.qmobile.network.Client;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tinf.qmobile.network.OnResponse.INDEX;
import static com.tinf.qmobile.network.OnResponse.PG_HOME;

public class WebViewActivity extends AppCompatActivity {
    private ActivityWebviewBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.getSettings().setUseWideViewPort(true);
        binding.webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if (!url.contains("javascript")) {
                    view.loadUrl("javascript:(function() {" +
                            "document.getElementsByTagName('tbody')[0].childNodes[0].style.display='none';" +
                            "document.getElementsByTagName('tbody')[0].childNodes[4].style.display='none';" +
                            "})()");
                }

                super.onPageFinished(view, url);
            }
        });

        binding.webview.loadUrl(Client.get().getURL() + INDEX + PG_HOME);
    }

}
