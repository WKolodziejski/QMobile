package com.tinf.qmobile.Class.Materias;

import com.tinf.qmobile.Class.Calendario.Evento;

import java.io.Serializable;
import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Materia implements Serializable {
    @Id public long id;
    private int color;
    private String name;
    private String faltas;
    @Transient private boolean isExpanded;
    @Transient private boolean anim;
    private int year;
    @Backlink(to = "materia") public ToMany<Etapa> etapas;
    @Backlink(to = "materia") public ToMany<Horario> horarios;
    @Backlink(to = "materia") public ToMany<Evento> eventos;
    @Backlink(to = "materia") public ToOne<Infos> infos;

    public Materia() {}

    public Materia(String name, int color, int year) {
        this.name = name.trim();
        this.color = color;
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaltas() {
        return faltas;
    }

    public void setFaltas(String faltas) {
        this.faltas = faltas;
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

    public long getId(){
        return id;
    }
}
