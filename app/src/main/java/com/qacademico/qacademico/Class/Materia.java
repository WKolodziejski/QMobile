package com.qacademico.qacademico.Class;

import java.io.Serializable;

public class Materia implements Serializable {
    private final String hora;
    private final String materia;

    public Materia(String hora, String materia) {
        this.hora = hora;
        this.materia = materia;
    }

    public String getHora() {
        return hora;
    }

    public String getMateria() {
        return materia;
    }

}
