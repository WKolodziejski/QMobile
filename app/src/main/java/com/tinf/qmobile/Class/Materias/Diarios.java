package com.tinf.qmobile.Class.Materias;

import java.io.Serializable;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class Diarios implements Serializable {
    @Id public long id;
    private String nome;
    private String peso;
    private String max;
    private String nota;
    private int tipo;
    private String data;
    private int tint;
    public ToOne<Etapa> etapa;

    public Diarios(String nome, String peso, String max, String nota, int tipo, String data, int tint) {
        this.nome = nome;
        this.peso = peso;
        this.max = max;
        this.nota = nota;
        this.tipo = tipo;
        this.data = data;
        this.tint = tint;
    }

    public Diarios() {}

    public long getId() {
        return id;
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

    public int getTipo() {
        return tipo;
    }

    public String getData() {
        return data;
    }

    public int getTint() {
        return tint;
    }
}
