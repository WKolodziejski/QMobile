package com.tinf.qacademico.Class.Materias;

import java.io.Serializable;

public class Diarios implements Serializable {
    private final String nome;
    private final String peso;
    private final String max;
    private final String nota;
    private final String tipo;
    private final String data;
    private final int tint;

    public Diarios(String nome, String peso, String max, String nota, String tipo, String data, int tint) {
        this.nome = nome;
        this.peso = peso;
        this.max = max;
        this.nota = nota;
        this.tipo = tipo;
        this.data = data;
        this.tint = tint;
    }

    public String getNome() {
        return nome;
    }

    public String getPeso() {
        return peso;
    }

    public String getMax() {
        return max;
    }

    public String getNota() {
        return nota;
    }

    public String getTipo() {
        return tipo;
    }

    public String getData() {
        return data;
    }

    public int getTint() {
        return tint;
    }
}
