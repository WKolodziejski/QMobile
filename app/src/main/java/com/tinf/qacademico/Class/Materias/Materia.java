package com.tinf.qacademico.Class.Materias;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.annotation.Unique;

@Entity
public class Materia implements Serializable {
    @Id public long id;
    @NonNull private List<Etapa> etapas;
    @NonNull private List<Horario> horarios = new ArrayList<>();
    @ColorInt private int color;
    @Unique private String name;
    private String totalFaltas;
    @Transient private boolean isExpanded;
    @Transient private boolean anim;

    public Materia(String name, int color, List<Etapa> etapas) {
        this.color = color;
        this.name = name.trim();
        this.etapas = etapas;
        Log.i(name, etapas.toString());
    }

    @NonNull
    public List<Horario> getHorarios() {
        return horarios;
    }

    public void setHorarios(@NonNull List<Horario> horarios) {
        this.horarios = horarios;
    }

    public void addHorario(Horario horario) {
        this.horarios.add(horario);
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

    @NonNull
    public List<Etapa> getEtapas() {
        return etapas;
    }

    public void setEtapas(@NonNull List<Etapa> etapas) {
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

    public long getId(){
        return id;
    }
}
