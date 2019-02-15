package com.tinf.qmobile.Network;

public interface OnResponse {
    String URL = "http://qacademico.ifsul.edu.br";
    String INDEX = "/qacademico/index.asp?t=";
    int PG_LOGIN = 1001;
    int PG_HOME = 2000;
    int PG_DIARIOS = 2071;
    int PG_BOLETIM = 2032;
    int PG_HORARIO = 2010;
    int PG_MATERIAIS = 2061;
    int PG_CALENDARIO = 2020;
    int PG_ERRO = 1;
    int PG_GERADOR = 2;
    int PG_ACESSO_NEGADO = 3;

    void onStart(int pg, int year);
    void onFinish(int pg, int year);
    void onError(int pg, String error);
    void onAccessDenied(int pg, String message);

}
