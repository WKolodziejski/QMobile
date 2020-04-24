package com.tinf.qmobile.network.handler;

import android.webkit.JavascriptInterface;
import com.tinf.qmobile.parser.BaseParser;
import com.tinf.qmobile.parser.MessageParser;

public class MessagesHandler {
    private BaseParser.OnFinish onFinish;
    private BaseParser.OnError onError;

    public MessagesHandler(BaseParser.OnFinish onFinish, BaseParser.OnError onError) {
        this.onFinish = onFinish;
        this.onError = onError;
    }

    @JavascriptInterface
    public void handlePage(String html) {
        new MessageParser(onFinish, onError).execute(html);
    }

}
