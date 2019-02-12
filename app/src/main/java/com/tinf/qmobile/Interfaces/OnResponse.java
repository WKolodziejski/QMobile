package com.tinf.qmobile.Interfaces;

public interface OnResponse {

    void onStart(int pg, int year);
    void onFinish(int pg, int year);
    void onError(int pg, String error);
    void onAccessDenied(int pg, String message);

}
