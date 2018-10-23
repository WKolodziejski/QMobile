package com.tinf.qacademico.Class.Materias;

import java.io.Serializable;
import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Etapa implements Serializable {
    @Id public long id;
    private String etapa;
    private String nota;
    private String notaRP;
    private String notaFinal;
    private String faltas;
    public ToOne<Materia> materia;
    @Backlink public ToMany<Diarios> diarios;
    @Backlink public ToMany<Aula> aulas;

    public Etapa() {}

    public Etapa(String etapa){
        this.etapa = etapa.trim();
    }

    public long getId() {
        return id;
    }

    public String getEtapa() {
        return etapa;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getNotaRP() {
        return notaRP;
    }

    public void setNotaRP(String notaRP) {
        this.notaRP = notaRP;
    }

    public String getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(String notaFinal) {
        this.notaFinal = notaFinal;
    }

    public String getFaltas() {
        return faltas;
    }

    public void setFaltas(String faltas) {
        this.faltas = faltas;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

}
