package com.tinf.qmobile.Class.Calendario;

import com.tinf.qmobile.Class.Materias.Matter;

import java.io.Serializable;

import androidx.annotation.ColorInt;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class Evento implements Serializable {
    @Id public long id;
    private String title;
    private String description;
    @ColorInt private int color;
    private String inicio;
    private String fim;
    private boolean happened;
    public boolean userEvent;
    public ToOne<Dia> day;
    public ToOne<Matter> materia;

    public Evento(String title, String description, int color, boolean userEvent) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.userEvent = userEvent;
    }

    public Evento(String title, String description, int color, String inicio, String fim, boolean userEvent) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.inicio = inicio;
        this.fim = fim;
        this.userEvent = userEvent;
    }

    /*
     * Required methods
     */

    public Evento() {}

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
