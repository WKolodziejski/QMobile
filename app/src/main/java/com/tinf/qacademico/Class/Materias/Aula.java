package com.tinf.qacademico.Class.Materias;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class Aula {
    @Id public long id;
    private String data;
    private String horario;
    private int aulas_dadas;
    private String faltas;
    private String conteudo;
    private ToOne<Etapa> etapa;

    public Aula(String data, String horario, int aulas_dadas, String faltas, String conteudo) {
        this.data = data;
        this.horario = horario;
        this.aulas_dadas = aulas_dadas;
        this.faltas = faltas;
        this.conteudo = conteudo;
    }

    public Aula() {}

    public ToOne<Etapa> getEtapa() {
        return etapa;
    }

    public long getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public int getAulas_dadas() {
        return aulas_dadas;
    }

    public void setAulas_dadas(int aulas_dadas) {
        this.aulas_dadas = aulas_dadas;
    }

    public String getFaltas() {
        return faltas;
    }

    public void setFaltas(String faltas) {
        this.faltas = faltas;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
}
