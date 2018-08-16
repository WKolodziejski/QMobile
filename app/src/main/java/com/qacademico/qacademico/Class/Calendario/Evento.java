package com.qacademico.qacademico.Class.Calendario;


import java.io.Serializable;
import java.util.List;

public class Evento implements Serializable {
    private String nome;
    private String horario;
    private int color;

    public Evento(String nome, String horario, int color) {
        this.nome = nome;
        this.horario = horario;
        this.color = color;
    }

    public Evento(String nome, int color) {
        this.nome = nome;
        this.color = color;
    }

    public String getNome() {
        return nome;
    }

    public String getHorario() {
        return horario;
    }

    public int getColor() {
        return color;
    }
}
