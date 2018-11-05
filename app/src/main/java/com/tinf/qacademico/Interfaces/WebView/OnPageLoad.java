package com.tinf.qacademico.Interfaces.WebView;

import java.util.List;

public interface OnPageLoad {

    interface Materiais {
        void onPageFinish(List<?> list);
    }

    interface Main {
        void onPageStart();
        void onPageFinish(String url_p);
        void onErrorRecived(String error);
    }
}
