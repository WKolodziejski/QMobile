package com.qacademico.qacademico.Class.Calendario;


import java.io.Serializable;
import java.util.List;

public class Dia implements Serializable {
    private List<Evento> eventos;
    private int dia;

    public Dia(int dia, List<Evento> eventos) {
        this.dia = dia;
        this.eventos = eventos;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public int getDia() {
        return dia;
    }
}
