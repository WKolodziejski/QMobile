package com.tinf.qmobile.network;

import android.webkit.WebView;

public interface OnEvent {

    void onRenewalAvailable();
    void onDialog(WebView webView, String title, String msg);

}
