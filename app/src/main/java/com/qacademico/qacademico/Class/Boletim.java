package com.qacademico.qacademico.Class;

import java.io.Serializable;

public class Boletim implements Serializable {
    private final String materia;
    private final String Tfaltas;
    private final String NotaPrimeiraEtapa;
    private final String FaltasPrimeiraEtapa;
    private final String RPPrimeiraEtapa;
    private final String NotaFinalPrimeiraEtapa;
    private final String NotaSegundaEtapa;
    private final String FaltasSegundaEtapa;
    private final String RPSegundaEtapa;
    private final String NotaFinalSeungaEtapa;
    private boolean isExpanded;
    private boolean anim;

    public Boletim(String materia, String tfaltas, String notaPrimeiraEtapa, String faltasPrimeiraEtapa, String RPPrimeiraEtapa,
                   String notaFinalPrimeiraEtapa, String notaSegundaEtapa, String faltasSegundaEtapa, String RPSegundaEtapa,
                   String notaFinalSeungaEtapa) {
        this.materia = materia;
        this.Tfaltas = tfaltas;
        this.NotaPrimeiraEtapa = notaPrimeiraEtapa;
        this.FaltasPrimeiraEtapa = faltasPrimeiraEtapa;
        this.RPPrimeiraEtapa = RPPrimeiraEtapa;
        this.NotaFinalPrimeiraEtapa = notaFinalPrimeiraEtapa;
        this.NotaSegundaEtapa = notaSegundaEtapa;
        this.FaltasSegundaEtapa = faltasSegundaEtapa;
        this.RPSegundaEtapa = RPSegundaEtapa;
        this.NotaFinalSeungaEtapa = notaFinalSeungaEtapa;
        this.anim = false;
        this.isExpanded = false;
    }

    public String getMateria() {
        return materia;
    }

    public String getTfaltas() {
        return Tfaltas;
    }

    public String getNotaPrimeiraEtapa() {
        return NotaPrimeiraEtapa;
    }

    public String getFaltasPrimeiraEtapa() {
        return FaltasPrimeiraEtapa;
    }

    public String getRPPrimeiraEtapa() {
        return RPPrimeiraEtapa;
    }

    public String getNotaFinalPrimeiraEtapa() {
        return NotaFinalPrimeiraEtapa;
    }

    public String getNotaSegundaEtapa() {
        return NotaSegundaEtapa;
    }

    public String getFaltasSegundaEtapa() {
        return FaltasSegundaEtapa;
    }

    public String getRPSegundaEtapa() {
        return RPSegundaEtapa;
    }

    public String getNotaFinalSeungaEtapa() {
        return NotaFinalSeungaEtapa;
    }

    public boolean getExpanded(){
        return isExpanded;
    }

    public void setExpanded(boolean expanded){
        isExpanded = expanded;
    }

    public boolean getAnim(){
        return anim;
    }

    public void setAnim(boolean anim){
        this.anim = anim;
    }
}