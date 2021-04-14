package com.tinf.qmobile.network.message;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.network.handler.MessagesHandler;

import java.util.ArrayList;
import java.util.List;

public class Messenger implements OnMessages, DownloadListener, OnResponse {
    private boolean isLoading;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
    private int pg;
    private final WebView webView;
    private final List<String> queue;
    private final OnResponse onResponse;
    private final Context context;

    public Messenger(Context context, OnResponse onResponse) {
        this.context = context;
        this.queue = new ArrayList<>();
        this.onResponse = onResponse;
        //this.webView = webView;

        webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setBlockNetworkImage(true);
        webView.addJavascriptInterface(new MessagesHandler(this, onResponse), "handler");
        webView.setDownloadListener(this);
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d("JS", message);
                onError(pg, message);
                return true;
            }

        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isLoading = true;
                onResponse.onStart(pg);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.equals(Client.get().getURL() + MESSAGES)) {
                    view.loadUrl(   "javascript:(function() {" +
                                        "var targetNode = document.getElementById('ctl00_UpdateProgressAguarde');" +
                                        "var config = {attributes: true};" +
                                        "var callback = function(mutationsList, observer) {" +
                                            "setTimeout(() => {" +
                                                "for(var mutation of mutationsList) {" +
                                                    "if (mutation.attributeName == 'aria-hidden') {" +
                                                        "if (mutation.target.attributes['aria-hidden'].value == 'true') {" +
                                                            "window.handler.handlePage('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');" +
                                                        "}" +
                                                    "}" +
                                                "}" +
                                            "}, 1000);" +
                                        "};" +
                                        "var observer = new MutationObserver(callback);" +
                                        "observer.observe(targetNode, config);" +
                                    "})()");

                    view.loadUrl(   "javascript:(function() {" +
                                        "window.handler.handlePage('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');" +
                                    "})()");

                    Log.d("HANDLER", "added");

                    isLoading = false;
                    onResponse.onFinish(pg);
                }
            }

        });

        if (Client.get().isLogging()) {
            isLoading = true;
            onResponse.onStart(PG_LOGIN);
            Client.get().addOnResponseListener(this);
        }
        else
            loadFirstPage();
    }

    public void loadFirstPage() {
        webView.loadUrl(Client.get().getURL() + MESSAGES);
    }

    public void loadPage(int p) {
        String request = "javascript:(function() {" +
                "__doPostBack('ctl00$ContentPlaceHolderPrincipal$wucMensagens1$grdMensagens','Page$" +
                p + "')})()";

        if (p != pg)
            if (p < pg || hasNextPage)
                if (isLoading)
                    enqueue(request);
                else {
                    isLoading = true;
                    onResponse.onStart(pg);
                    webView.loadUrl(request);
                }
    }

    public void loadNextPage() {
        String request = "javascript:(function() {" +
                "__doPostBack('ctl00$ContentPlaceHolderPrincipal$wucMensagens1$grdMensagens','Page$" +
                (pg + 1) + "')})()";

        if (hasNextPage)
            if (isLoading)
                enqueue(request);
            else {
                isLoading = true;
                onResponse.onStart(pg);
                webView.loadUrl(request);
            }
    }

    public void loadPreviousPage() {
        String request = "javascript:(function() {" +
                "__doPostBack('ctl00$ContentPlaceHolderPrincipal$wucMensagens1$grdMensagens','Page$" +
                (pg - 1) + "')})()";

        if (hasPreviousPage)
            if (isLoading)
                enqueue(request);
            else {
                isLoading = true;
                onResponse.onStart(pg);
                webView.loadUrl(request);
            }
    }

    public void openMessage(int i) {
        String request = "javascript:(function() {" +
                "__doPostBack('ctl00$ContentPlaceHolderPrincipal$wucMensagens1$grdMensagens','exibir_mensagem$" +
                (i % 20) + "')})()";

        int p = Math.round(i / 20) + 1;

        Log.d("Index_", String.valueOf(p));

        if (p != pg) {
            loadPage(p);
            enqueue(request);
        } else {
            isLoading = true;
            onResponse.onStart(pg);
            webView.loadUrl(request);
        }
    }

    private void enqueue(String request) {
        if (!queue.contains(request)) {
            queue.add(request);
            Log.d("Enqueued", request);
        }
    }

    @Override
    public void onFinish(int pg, boolean hasMorePages) {
        this.pg = pg;
        this.hasNextPage = hasMorePages;
        this.hasPreviousPage = pg != 1;
        this.isLoading = false;

        if (!queue.isEmpty())
            webView.post(() -> {
                isLoading = true;
                onResponse.onStart(pg);
                webView.loadUrl(queue.remove(0));
            });

        Log.d(String.valueOf(pg), String.valueOf(hasMorePages));
    }

    @Override
    public void onFinish() {
        this.isLoading = false;

        if (!queue.isEmpty())
            webView.post(() -> {
                isLoading = true;
                onResponse.onStart(pg);
                webView.loadUrl(queue.remove(0));
            });
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        /*DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadReceiver.id = dm.enqueue(new DownloadManager.Request(Uri.parse(url))
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverRoaming(false)
                .setMimeType("application/pdf")
                .setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype))
                .addRequestHeader("Content-Disposition", "attachment; filename=" + URLUtil.guessFileName(url, contentDisposition, mimetype))
                .setDescription(contentDisposition)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        PATH + "/" + url));
        onFinish(pg, 0);*/
    }

    @Override
    public void onStart(int pg) {

    }

    @Override
    public void onFinish(int pg) {
        if (pg == PG_LOGIN) {
            loadFirstPage();
            Client.get().removeOnResponseListener(this);
        }
    }

    @Override
    public void onError(int pg, String error) {
        if (pg == PG_ERROR)
            Client.get().login();

        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
        onResponse.onError(pg, error);
    }

    @Override
    public void onAccessDenied(int pg, String message) {
        onResponse.onAccessDenied(pg, message);
    }

}
