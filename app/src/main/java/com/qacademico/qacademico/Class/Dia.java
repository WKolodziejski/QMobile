package com.qacademico.qacademico.Class;


import java.io.Serializable;
import java.util.List;

public class Dia implements Serializable {
    private String dia;
    private String cor;
    private List<String> eventos;

    private boolean isExpanded;
    private boolean anim;

    public Dia(String dia, String cor, List<String> eventos) {
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
