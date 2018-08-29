package com.tinf.qacademico.Class.Calendario;


import java.io.Serializable;

public class Evento implements Serializable {
    private String description;
    private String title;
    private int color;
    private String inicio;
    private String fim;
    private boolean happened;

    public Evento(String title, String description, int color) {
        this.title = title;
        this.description = description;
        this.color = color;
    }

    public Evento(String title, String description, int color, String inicio, String fim){
        this.title = title;
        this.description = description;
        this.color = color;
        this.inicio = inicio ;
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

    public boolean hasHappened() {
        return happened;
    }

    public void setHappened(boolean happened) {
        this.happened = happened;
    }
}
