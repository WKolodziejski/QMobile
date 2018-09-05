package com.tinf.qacademico.Class.Materias;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Unique;


public class Materia implements Serializable {
    //public long id;
    private List<Etapa> etapas;
    private List<Horario> horarios;
    private int color;
    private String name;
    private String totalFaltas;
    transient private boolean isExpanded;
    transient private boolean anim;

    public Materia(String name, int color, List<Etapa> etapas) {
        this.color = color;
        this.name = name.trim();
        this.etapas = etapas;
    }

    public List<Horario> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<Horario> horarios) {
        this.horarios = horarios;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Etapa> getEtapas() {
        return etapas;
    }

    public void setEtapas(List<Etapa> etapas) {
        this.etapas = etapas;
    }

    public String getTotalFaltas() {
        return totalFaltas;
    }

    public void setTotalFaltas(String totalFaltas) {
        this.totalFaltas = totalFaltas;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public boolean isAnim() {
        return anim;
    }

    public void setAnim(boolean anim) {
        this.anim = anim;
    }
}
