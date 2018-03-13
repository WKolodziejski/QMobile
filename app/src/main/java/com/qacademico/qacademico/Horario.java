package com.qacademico.qacademico;

import java.util.List;

public class Horario {
    private final String dia;
    private final List<Materia> materiasList;
    private boolean isExpanded;
    private boolean anim;

    public Horario(String dia, List<Materia> materiasList) {
        this.dia = dia;
        this.materiasList = materiasList;
        this.anim = false;
        this.isExpanded = true;
    }

    public String getDia() {
        return dia;
    }

    public List<Materia> getMateriasList() {
        return materiasList;
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