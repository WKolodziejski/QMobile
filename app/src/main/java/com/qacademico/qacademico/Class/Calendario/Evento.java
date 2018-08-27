package com.qacademico.qacademico.Class.Calendario;


import java.io.Serializable;

public class Evento implements Serializable {
    private String description;
    private String title;
    private int color;

    public Evento(String title, String description, int color) {
        this.title = title;
        this.description = description;
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public int getColor() {
        return color;
    }
}
