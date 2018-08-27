package com.qacademico.qacademico.Class.Calendario;


import java.io.Serializable;
import java.util.List;

public class Meses implements Serializable {
    private List<Dia> dias;
    private int month;
    private int year;

    public Meses(List<Dia> dias, int month, int year) {
        this.dias = dias;
        this.month = month;
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public List<Dia> getDias() {
        return dias;
    }

    public int getYear() {
        return year;
    }
}
