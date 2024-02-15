package com.tinf.qmobile.network;

public interface OnEvent {
  void onRenewalAvailable();

  void onDialog(String title,
                String msg);
}
