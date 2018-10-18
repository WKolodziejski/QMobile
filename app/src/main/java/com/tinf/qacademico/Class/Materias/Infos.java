package com.tinf.qacademico.Class.Materias;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToOne;

@Entity
public class Infos implements Serializable {
    @Id public long id;
    private int carga_horaria;
    private int total_aulas;
    private int aulas_ministradas;
    private int auals_restantes;
    private int faltas;
    private int presenca_prevista;
    private int presenca_obrigatoria;
    @Unique private int cod;
    private String professor;
    private ToOne<Materia> materia;

    public Infos(int carga_horaria, int total_aulas, int aulas_ministradas, int auals_restantes, int faltas, int presenca_prevista, int presenca_obrigatoria, int cod, String professor) {
        this.carga_horaria = carga_horaria;
        this.total_aulas = total_aulas;
        this.aulas_ministradas = aulas_ministradas;
        this.auals_restantes = auals_restantes;
        this.faltas = faltas;
        this.presenca_prevista = presenca_prevista;
        this.presenca_obrigatoria = presenca_obrigatoria;
        this.cod = cod;
        this.professor = professor;
    }

    public Infos() {}

    public ToOne<Materia> getMateria() {
        return materia;
    }

    public long getId() {
        return id;
    }

    public int getCarga_horaria() {
        return carga_horaria;
    }

    public void setCarga_horaria(int carga_horaria) {
        this.carga_horaria = carga_horaria;
    }

    public int getTotal_aulas() {
        return total_aulas;
    }

    public void setTotal_aulas(int total_aulas) {
        this.total_aulas = total_aulas;
    }

    public int getAulas_ministradas() {
        return aulas_ministradas;
    }

    public void setAulas_ministradas(int aulas_ministradas) {
        this.aulas_ministradas = aulas_ministradas;
    }

    public int getAuals_restantes() {
        return auals_restantes;
    }

    public void setAuals_restantes(int auals_restantes) {
        this.auals_restantes = auals_restantes;
    }

    public int getFaltas() {
        return faltas;
    }

    public void setFaltas(int faltas) {
        this.faltas = faltas;
    }

    public int getPresenca_prevista() {
        return presenca_prevista;
    }

    public void setPresenca_prevista(int presenca_prevista) {
        this.presenca_prevista = presenca_prevista;
    }

    public int getPresenca_obrigatoria() {
        return presenca_obrigatoria;
    }

    public void setPresenca_obrigatoria(int presenca_obrigatoria) {
        this.presenca_obrigatoria = presenca_obrigatoria;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }
}
