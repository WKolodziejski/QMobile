package com.tinf.qacademico.Class;

import com.tinf.qacademico.Class.Diarios.DiariosList;
import java.io.Serializable;

public class Materia implements Serializable {
    private DiariosList diarios;
    private Boletim boletim;
    private Horario horario;
    private int color;
    private String name;

    public Materia(String name, int color) {
        this.color = color;
        this.name = name;
    }

    public DiariosList getDiarios() {
        return diarios;
    }

    public void setDiarios(DiariosList diarios) {
        this.diarios = diarios;
    }

    public Boletim getBoletim() {
        return boletim;
    }

    public void setBoletim(Boletim boletim) {
        this.boletim = boletim;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
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
}
