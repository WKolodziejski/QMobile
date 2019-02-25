package com.tinf.qmobile.Class.Materias;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class Horario implements Serializable {
    @Id public long id;
    private int day;
    private String time;
    private boolean isFromSite;
    public ToOne<Matter> materia;

    public Horario(int day, String time, boolean isFromSite) {
        this.day = day;
        this.time = time;
        this.isFromSite = isFromSite;
    }

    public Horario() {}

    public long getId() {
        return id;
    }

    public int getDay() {
        return day;
    }

    public int getStartHour() {
        return trimh(trimta(time));
    }

    public int getEndHour() {
        return trimh(trimtd(time));
    }

    public int getStartMinute() {
        return trimm(trimta(time));
    }

    public int getEndMinute() {
        return trimm(trimtd(time));
    }

    public String getTime() {
        return time;
    }

    public boolean isFromSite() {
        return isFromSite;
    }

    private int trimh(String string) {
        string = string.substring(0, string.indexOf(":"));
        return Integer.valueOf(string);
    }

    private int trimm(String string) {
        string = string.substring(string.indexOf(":") + 1);
        return Integer.valueOf(string);
    }

    private String trimta(String string) {
        string = string.substring(0, string.indexOf("~"));
        return string;
    }

    private String trimtd(String string) {
        string = string.substring(string.indexOf("~") + 1);
        return string;
    }
}
