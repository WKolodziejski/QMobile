package com.tinf.qmobile.network;

public interface OnEvent {

    void onMessage(int count);
    void onRenewalAvailable();
    void onJournal(int count);
    void onDialog(String title, String msg);

}