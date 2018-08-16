package com.qacademico.qacademico.Class.Diarios;

import com.qacademico.qacademico.Class.Calendario.Dia;

import java.io.Serializable;
import java.util.List;

public class DiariosList implements Serializable {
    private final String title;
    private final List<Etapa> etapas;
    transient private boolean isExpanded;
    transient private boolean anim;

    public DiariosList(String title, List<Etapa> etapas) {
        this.title = title;
        this.etapas = etapas;
        this.anim = false;
        this.isExpanded = false;
    }

    public String getTitle() {
        return title;
    }

    public List<Etapa> getEtapas() {
        return etapas;
    }

    public boolean getExpanded(){
        return isExpanded;
    }

    public void setExpanded(boolean expanded){
        isExpanded = expanded;
    }

    public boolean getAnim(){
        return anim;
    }

    public void setAnim(boolean anim){
        this.anim = anim;
    }
}
