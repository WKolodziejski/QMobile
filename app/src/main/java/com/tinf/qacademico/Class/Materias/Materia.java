package com.tinf.qacademico.Class.Materias;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import android.util.Log;

import com.tinf.qacademico.Utilities.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Materia implements Serializable {
    @Id public long id;
    private int color;
    @Unique private String name;
    private String totalFaltas;
    @Transient private boolean isExpanded;
    @Transient private boolean anim;
    private int year;
    @Backlink(to = "materia") public ToMany<Etapa> etapas;
    @Backlink public ToMany<Horario> horarios;
    private ToOne<Infos> infos;

    public Materia() {}

    public Materia(String name, int color, int year) {
        this.name = name.trim();
        this.color = color;
        this.year = year;
    }

    public ToOne<Infos> getInfos() {
        return infos;
    }

    public ToMany<Etapa> getEtapas() {
        return etapas;
    }

    public ToMany<Horario> getHorarios() {
        return horarios;
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

    public long getId(){
        return id;
    }
}
