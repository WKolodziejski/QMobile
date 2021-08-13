package com.tinf.qmobile.network.handler;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.tinf.qmobile.R;
import com.tinf.qmobile.network.OnEvent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static com.tinf.qmobile.App.getContext;

public class PopUpHandler {
    private final WebView webView;
    private final OnEvent onEvent;
    private boolean requested;

    public PopUpHandler(WebView webView, OnEvent onEvent, boolean requested) {
        this.webView = webView;
        this.onEvent = onEvent;
        this.requested = requested;
    }

    @JavascriptInterface
    public void handleLogin(String page) {
        Document document = Jsoup.parse(page);

        Element body = document.getElementById("modalmensagens");

        document.outputSettings(new Document.OutputSettings().prettyPrint(false));
        document.select("br").after("\\n");

        if (body != null) {
            String title = body.getElementsByClass("subtitulo").first().text().replaceAll("\\\\n", "\n").trim();
            String message = body.getElementsByClass("conteudoTexto").first().text().replaceAll("\\\\n", "\n").trim();

            onEvent.onDialog(webView, title, message);
        } else if (requested) {
            Toast.makeText(getContext(), getContext().getString(R.string.dialog_no_alerts), Toast.LENGTH_SHORT).show();
        }

        Elements conteudoLink = document.getElementsByClass("conteudoLink");

        if (conteudoLink.size() >= 3) {

            Element renewal = conteudoLink.get(2);

            if (renewal != null) {

                if (renewal.text().contains("matrícula")) {
                    onEvent.onRenewalAvailable();
                }
            }
        }
    }

}
