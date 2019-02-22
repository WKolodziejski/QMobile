package com.tinf.qmobile.Class.Materias;

import com.tinf.qmobile.Class.Calendario.Evento;
import com.tinf.qmobile.Class.Materiais.Material;

import java.io.Serializable;

import androidx.annotation.ColorInt;
import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Materia implements Serializable {
    @Id public long id;
    @ColorInt private int color;
    private String name;
    private String faltas;
    @Transient private boolean isExpanded;
    @Transient private boolean anim;
    private int year;
    private int period;
    @Backlink(to = "materia") public ToMany<Etapa> etapas;
    @Backlink(to = "materia") public ToMany<Horario> horarios;
    @Backlink(to = "materia") public ToMany<Evento> eventos;
    @Backlink(to = "materia") public ToMany<Material> materiais;
    @Backlink(to = "materia") public ToOne<Infos> infos;

    public Materia() {}

    public Materia(String name, int color, int year, int period) {
        this.name = name.trim();
        this.color = color;
        this.year = year;
        this.period = period;
    }

    public int getYear() {
        return year;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name.trim();
    }

    public String getFaltas() {
        return faltas == null ? "" : faltas;
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

    public int getPeriod() {
        return period;
    }

}
