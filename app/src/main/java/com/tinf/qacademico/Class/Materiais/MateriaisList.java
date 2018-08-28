package com.tinf.qacademico.Class.Materiais;

import java.io.Serializable;
import java.util.List;

public class MateriaisList implements Serializable{
    private final String title;
    private final List<Materiais> materiais;
    private final int color;

    public MateriaisList(String title, List<Materiais> materiais, int color) {
        this.title = title;
        this.materiais = materiais;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public List<Materiais> getMateriais() {
        return materiais;
    }

    public int getColor() {
        return color;
    }
}
