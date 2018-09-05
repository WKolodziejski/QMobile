package com.tinf.qacademico.Class.Materias;

import android.util.Log;

import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.converter.PropertyConverter;

public class Horario implements Serializable {
    //public long id;
    private int day;
    private String time;

    public Horario(int day, String time) {
        this.day = day;
        this.time = time;
        Log.v("New Horário", day + time);
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
