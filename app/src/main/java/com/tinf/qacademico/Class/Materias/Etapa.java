package com.tinf.qacademico.Class.Materias;

/*import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;*/

import com.tinf.qacademico.Class.Materias.Diarios;

import java.io.Serializable;
import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;


public class Etapa implements Serializable {
    //public long id;
    private String etapa;
    private List<Diarios> diariosList;
    private String nota;
    private String notaRP;
    private String notaFinal;
    private String faltas;

    public Etapa(String etapa, List<Diarios> diariosList){
        this.etapa = etapa.trim();
        this.diariosList = diariosList;
    }

    public List<Diarios> getDiariosList() {
        return diariosList;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setDiariosList(List<Diarios> diariosList) {
        this.diariosList = diariosList;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getNotaRP() {
        return notaRP;
    }

    public void setNotaRP(String notaRP) {
        this.notaRP = notaRP;
    }

    public String getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(String notaFinal) {
        this.notaFinal = notaFinal;
    }

    public String getFaltas() {
        return faltas;
    }

    public void setFaltas(String faltas) {
        this.faltas = faltas;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }
}
