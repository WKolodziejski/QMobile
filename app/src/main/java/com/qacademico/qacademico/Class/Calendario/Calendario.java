package com.qacademico.qacademico.Class.Calendario;


import java.io.Serializable;
import java.util.List;

public class Calendario implements Serializable {
    private String nomeMes = "";
    private final List<Dia> dias;
    private boolean isExpanded;
    private boolean anim;

    public Calendario(String nomeMes, List<Dia> dias) {
        this.nomeMes = nomeMes;
        this.dias = dias;
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
