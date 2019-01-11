package com.tinf.qmobile.Interfaces.WebView;
import com.tinf.qmobile.Class.Materiais.MateriaisList;

import java.util.List;

public interface OnPageLoad {

    interface Materiais {
        void onPageFinish(List<MateriaisList> list);
    }

    interface Main {
        void onPageStart();
        void onPageFinish(String url_p);
        void onErrorRecived(String url_p, String error);
    }
}
