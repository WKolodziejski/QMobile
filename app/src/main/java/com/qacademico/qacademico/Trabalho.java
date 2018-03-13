package com.qacademico.qacademico;

public class Trabalho {
    private final String nome;
    private final String peso;
    private final String max;
    private final String nota;
    private final String tipo;
    private final int tint;

    public Trabalho(String nome, String peso, String max, String nota, String tipo, int tint) {
        this.nome = nome;
        this.peso = peso;
        this.max = max;
        this.nota = nota;
        this.tipo = tipo;
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

    public int getTint() {
        return tint;
    }
}
