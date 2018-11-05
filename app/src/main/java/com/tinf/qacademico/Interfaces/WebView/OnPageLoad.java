package com.tinf.qacademico.Interfaces.WebView;

import java.util.List;

public interface OnPageLoad {
    void onPageStart(String url_p);
    void onPageFinish(List<?> list);
    void onPageFinish(String url_p);
    void onErrorRecived(String error);
}
