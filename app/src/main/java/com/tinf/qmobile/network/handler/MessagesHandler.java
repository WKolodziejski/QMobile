package com.tinf.qmobile.network.handler;

import android.webkit.JavascriptInterface;

import com.tinf.qmobile.network.OnResponse;
import com.tinf.qmobile.network.message.OnMessages;
import com.tinf.qmobile.parser.MessageParser;

public class MessagesHandler {
    private OnMessages onMessages;
    private OnResponse onResponse;

    public MessagesHandler(OnMessages onMessages, OnResponse onResponse) {
        this.onMessages = onMessages;
        this.onResponse = onResponse;
    }

    @JavascriptInterface
    public void handlePage(String html) {
        new MessageParser(onMessages, onResponse).execute(html);
    }

}
