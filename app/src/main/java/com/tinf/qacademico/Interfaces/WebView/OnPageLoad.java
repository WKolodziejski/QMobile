package com.tinf.qacademico.Interfaces.WebView;
import com.tinf.qacademico.Class.Materiais.MateriaisList;

import java.util.List;

public interface OnPageLoad {

    interface Materiais {
        void onPageFinish(List<MateriaisList> list);
    }

    interface Main {
        void onPageStart();
        void onPageFinish(String url_p);
        void onErrorRecived(String error);
    }
}
