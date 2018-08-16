package com.qacademico.qacademico.Class;

import java.io.Serializable;
import java.util.Calendar;

public class Horario implements Serializable {
    private Calendar startTime;
    private Calendar endTime;
    private String name;
    private int color;

    public Horario(String name, Calendar startTime, Calendar endTime, int color) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.color = color;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
