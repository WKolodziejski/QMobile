package com.tinf.qmobile.network.handler;

import android.util.Log;
import android.webkit.JavascriptInterface;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MessagesHandler {

    @JavascriptInterface
    public void handlePage(String html) {
        Document document = Jsoup.parse(html);

        Element text = document.getElementsByTag("textarea").first();

        if (text != null) {
            Log.d("Message", text.text());

            //TODO handle message
        } else {
            //TODO handle page change
        }

    }

}
