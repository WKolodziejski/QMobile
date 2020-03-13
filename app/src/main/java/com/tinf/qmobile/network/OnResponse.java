package com.tinf.qmobile.network;

public interface OnResponse {
    String IFSUL = "http://qacademico.ifsul.edu.br";
    String IFES = "https://academico3.cefetes.br";
    String IFMT = "https://academico.ifmt.edu.br";
    String INDEX = "/qacademico/index.asp?t=";
    int PG_LOGIN = 1001;
    int PG_HOME = 2000;
    int PG_JOURNALS = 2071;
    int PG_REPORT = 2032;
    int PG_SCHEDULE = 2010;
    int PG_MATERIALS = 2061;
    int PG_CALENDAR = 2020;
    int PG_ERROR = 1;
    int PG_GENERATOR = 2;
    int PG_ACCESS_DENIED = 3;
    int PG_FETCH_YEARS = 4;
    int PG_CLASSES = 5;

    void onStart(int pg, int pos);
    void onFinish(int pg, int pos);
    void onError(int pg, String error);
    void onAccessDenied(int pg, String message);

}
