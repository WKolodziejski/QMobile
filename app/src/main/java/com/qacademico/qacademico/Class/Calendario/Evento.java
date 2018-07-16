package com.qacademico.qacademico.Class.Calendario;


import java.io.Serializable;
import java.util.List;

public class Evento implements Serializable {
    private String dia;
    private String cor;
    private List<String> eventos;


    /**
     *
     *      FALTA IMPLEMENTAR
     *      
     *
     */
    private boolean isExpanded;
    private boolean anim;

    public Evento(String dia, String cor, List<String> eventos) {
        this.dia = dia;
        this.cor = cor;
        this.eventos = eventos;
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
