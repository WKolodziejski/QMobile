package com.tinf.qmobile.activity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tinf.qmobile.R;
import com.tinf.qmobile.network.Client;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tinf.qmobile.network.OnResponse.INDEX;
import static com.tinf.qmobile.network.OnResponse.PG_HOME;
import static com.tinf.qmobile.network.OnResponse.PG_LOGIN;

public class WebViewActivity extends AppCompatActivity {
    @BindView(R.id.webview)     WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        ButterKnife.bind(this);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookies(aBoolean -> {
            String cookie = Client.get().getHeaders().get("Cookie");
            String setCookie = Client.get().getHeaders().get("Set-Cookie");

            cookieManager.setCookie(Client.get().getURL(), cookie);
            cookieManager.setCookie(Client.get().getURL(), setCookie);

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.setWebViewClient(new WebViewClient() {
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

            webView.loadUrl(Client.get().getURL() + INDEX + PG_HOME, Client.get().getHeaders());

        });
    }

}
