package com.tinf.qmobile.network;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class JavaScriptHandler {
    private WebView webView;
    private OnEvent onEvent;

    public JavaScriptHandler(WebView webView, OnEvent onEvent) {
        this.webView = webView;
        this.onEvent = onEvent;
    }

    @JavascriptInterface
    public void handleLogin(String page) {
        Document document = Jsoup.parse(page);

        Element body = document.getElementById("modalmensagens");

        document.outputSettings(new Document.OutputSettings().prettyPrint(false));
        document.select("br").after("\\n");

        if (body != null) {
            String title = body.getElementsByClass("subtitulo").first().text().replaceAll("\\\\n", "\n").trim();
            String message = body.getElementsByClass("conteudoTexto").first().getElementsByTag("p").first().text().replaceAll("\\\\n", "\n").trim();

            onEvent.onDialog(webView, title, message);
        }

        Element renewal = document.getElementsByClass("conteudoLink").get(2);

        if (renewal != null) {

            if (renewal.text().contains("matrícula")) {
                onEvent.onRenewalAvailable();
            }
        }
    }

}
