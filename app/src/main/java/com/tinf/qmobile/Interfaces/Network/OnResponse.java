package com.tinf.qmobile.Interfaces.Network;

import com.android.volley.VolleyError;

public interface OnResponse {

    void OnFinish(String url, String response);
    void OnError(String url, VolleyError error);
}
