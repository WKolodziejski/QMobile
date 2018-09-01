package com.tinf.qacademico.Class.Materias;

import java.io.Serializable;
import java.util.Calendar;

public class Horario implements Serializable {
    private Calendar startTime;
    private Calendar endTime;

    public Horario(Calendar startTime, Calendar endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }
}
