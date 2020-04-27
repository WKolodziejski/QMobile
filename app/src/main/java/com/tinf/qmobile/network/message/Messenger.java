package com.tinf.qmobile.network.message;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.tinf.qmobile.network.Client;
import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.network.handler.MessagesHandler;
import com.tinf.qmobile.utility.User;
import java.util.ArrayList;
import java.util.List;
import static android.content.Context.DOWNLOAD_SERVICE;
import static com.tinf.qmobile.utility.User.REGISTRATION;

public class Messenger implements OnMessages, DownloadListener, OnResponse {
    private boolean isLoading;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
    private int pg;
    private WebView webView;
    private List<String> queue;
    private OnResponse onResponse;
    private Context context;

    public Messenger(Context context, OnResponse onResponse, WebView webView) {
        this.context = context;
        this.queue = new ArrayList<>();
        this.webView = webView;
        this.onResponse = onResponse;

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setBlockNetworkImage(true);
        webView.addJavascriptInterface(new MessagesHandler(this, onResponse), "handler");
        webView.setDownloadListener(this);
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d("JS", message);
                onResponse.onError(pg, message);
                return false;
            }

        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isLoading = true;
                onResponse.onStart(pg, 0);
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
                    onResponse.onFinish(pg, 0);
                }
            }

        });

        if (Client.get().isLogging()) {
            isLoading = true;
            onResponse.onStart(PG_LOGIN, 0);
            Client.get().addOnResponseListener(this);
        }
        else
            load();
    }

    public void load() {
        webView.loadUrl(Client.get().getURL() + MESSAGES);
    }

    private void loadPage(int p) {
        String request = "javascript:(function() {" +
                "__doPostBack('ctl00$ContentPlaceHolderPrincipal$wucMensagens1$grdMensagens','Page$" +
                p + "')})()";

        if (p != pg)
            if (isLoading)
                enqueue(request);
            else {
                isLoading = true;
                onResponse.onStart(pg, 0);
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
                onResponse.onStart(pg, 0);
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
                onResponse.onStart(pg, 0);
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
            onResponse.onStart(pg, 0);
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
                onResponse.onStart(pg, 0);
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
                onResponse.onStart(pg, 0);
                webView.loadUrl(queue.remove(0));
            });
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(new DownloadManager.Request(Uri.parse(url))
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverRoaming(false)
                .setMimeType("application/pdf")
                .setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype))
                .addRequestHeader("Content-Disposition", "attachment; filename=" + URLUtil.guessFileName(url, contentDisposition, mimetype))
                .setDescription(contentDisposition)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        "QMobile/" + User.getCredential(REGISTRATION) + "/" + url));
        onFinish(pg, 0);
    }

    @Override
    public void onStart(int pg, int pos) {

    }

    @Override
    public void onFinish(int pg, int pos) {
        if (pg == PG_LOGIN) {
            load();
            Client.get().removeOnResponseListener(this);
        }
    }

    @Override
    public void onError(int pg, String error) {
        onResponse.onError(pg, error);
    }

    @Override
    public void onAccessDenied(int pg, String message) {
        onResponse.onAccessDenied(pg, message);
    }

}
