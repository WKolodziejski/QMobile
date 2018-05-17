package com.qacademico.qacademico.Class;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public class Horario implements Serializable {
    private String materia;
    private int day;
    private String date;
    private int color;

    public Horario(String materia, int day, String date, int color) {
        this.materia = materia;
        this.day = day;
        this.date = date;
        this.color = color;
    }

    public String getMateria() {
        return materia;
    }

    public int getDay() {
        return day;
    }

    public String getDate() {
        return date;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}