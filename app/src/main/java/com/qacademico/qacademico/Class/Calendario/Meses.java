package com.qacademico.qacademico.Class.Calendario;


import java.io.Serializable;
import java.util.List;

public class Meses implements Serializable {
    private List<Dia> dia;
    private String nomeMes;

    public Meses(List<Dia> dia, String nomeMes) {
        this.dia = dia;
        this.nomeMes = nomeMes;
    }

    public String getNomeMes() {
        return nomeMes;
    }

    public void setNomeMes(String nomeMes) {
        this.nomeMes = nomeMes;
    }
}
