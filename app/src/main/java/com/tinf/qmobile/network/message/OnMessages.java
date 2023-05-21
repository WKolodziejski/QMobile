package com.tinf.qmobile.network.message;

public interface OnMessages {

  void onFinish(int pg, boolean hasMorePages);

  void onFinish();

}
