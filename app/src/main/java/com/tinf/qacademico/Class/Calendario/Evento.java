package com.tinf.qacademico.Class.Calendario;


import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class Evento implements Serializable {
    @Id public long id;
    private String description;
    private String title;
    private int color;
    private String inicio;
    private String fim;
    private boolean happened;
    public ToOne<Dia> day;

    public Evento(){};

    public Evento(String title, String description, int color) {
        this.title = title;
        this.description = description;
        this.color = color;
    }

    public Evento(String title, String description, int color, String inicio, String fim){
        this.title = title;
        this.description = description;
        this.color = color;
        this.inicio = inicio;
        this.fim = fim;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public int getColor() { return color; }

    public String getInicio() { return inicio; }

    public String getFim() { return fim;  }

    public boolean getHappened() {
        return happened;
    }

    public void setHappened(boolean happened) {
        this.happened = happened;
    }
}
