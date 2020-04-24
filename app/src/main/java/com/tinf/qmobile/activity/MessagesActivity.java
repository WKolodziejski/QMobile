package com.tinf.qmobile.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.tinf.qmobile.R;
import com.tinf.qmobile.fragment.MessagesFragment;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.handler.MessagesHandler;
import static com.tinf.qmobile.network.OnResponse.MESSAGES;

public class MessagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.messages_fragment, new MessagesFragment())
                .commit();

        //WebView webView = new WebView(getApplicationContext());
        WebView webView = findViewById(R.id.webview2);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setBlockNetworkImage(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.equals(Client.get().getURL() + MESSAGES)) {
                    view.loadUrl("javascript:(function() {" +
                            "var targetNode = document.getElementById('ctl00_UpdateProgressAguarde');" +
                            "var config = {attributes: true};" +
                            "var callback = function(mutationsList, observer) {" +
                            "for(var mutation of mutationsList) {" +
                            "if (mutation.attributeName == 'aria-hidden') {" +
                            "if (mutation.target.attributes['aria-hidden'].value == 'true') {" +
                            "window.handler.handlePage('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');}}}};" +
                            "var observer = new MutationObserver(callback);" +
                            "observer.observe(targetNode, config);" +
                            "})()");

                    view.loadUrl("javascript:(function() {" +
                            "window.handler.handlePage('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');" +
                            "})()");
                }
            }

        });
        webView.addJavascriptInterface(new MessagesHandler((pg, year) -> {

        }, (pg, error) -> {

        }), "handler");
        webView.loadUrl(Client.get().getURL() + MESSAGES);
    }

}