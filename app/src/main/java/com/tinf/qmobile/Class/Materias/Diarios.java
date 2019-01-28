package com.tinf.qmobile.Class.Materias;

import android.util.Log;

import com.tinf.qmobile.R;

import java.io.Serializable;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class Diarios implements Serializable {
    public enum Tipo {
        AVALIACAO(0), PROVA(1), TRABALHO(2), EXERCICIO(3), QUALITATIVA(4);

        private int anInt;

        Tipo(final int anInt) {
            this.anInt = anInt;
        }

        public int getInt() {
            return anInt;
        }
    }
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

    public int getTipoID() {
        if (tipo == Tipo.AVALIACAO.getInt()) {
            return R.string.sigla_Avaliacao;
        } else if (tipo == Tipo.PROVA.getInt()) {
            return R.string.sigla_Prova;
        } else if (tipo == Tipo.EXERCICIO.getInt()) {
            return R.string.sigla_Exercicio;
        } else if (tipo == Tipo.TRABALHO.getInt()) {
            return R.string.sigla_Trabalho;
        } else if (tipo == Tipo.QUALITATIVA.getInt()) {
            return R.string.sigla_Qualitativa;
        } else return tipo;
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

    public Etapa getEtapa() {
        return etapa.getTarget();
    }
}
