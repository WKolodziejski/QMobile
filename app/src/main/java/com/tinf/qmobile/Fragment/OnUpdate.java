package com.tinf.qmobile.Fragment;

public interface OnUpdate {
    int UPDATE_REQUEST = 0;

    void onUpdate(int pg);
    void onScrollRequest();
}
