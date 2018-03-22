package com.qacademico.qacademico;


import java.io.Serializable;
import java.util.List;

public class Materiais implements Serializable {
    private final String nomeMateria;
    private final List<Material> materialList;
    private boolean isExpanded;
    private boolean anim;

    public Materiais(String nomeMateria, List<Material> materialList) {
        this.nomeMateria = nomeMateria;
        this.materialList = materialList;
        this.anim = false;
        this.isExpanded = false;
    }

    public String getNomeMateria() {
        return nomeMateria;
    }

    public List<Material> getMaterialList() {
        return materialList;
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
