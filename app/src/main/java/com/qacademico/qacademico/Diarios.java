package com.qacademico.qacademico;

/*import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;*/
import android.content.Context;

import java.util.List;

public class Diarios /*implements Parcelable*/{
    private final String nomeMateria;
    private final List<Etapa> etapaList; //COSTUMAVA SER FINAL
    private boolean isExpanded;
    private boolean anim;

    public Diarios(String nomeMateria, List<Etapa> etapaList, Context context) {
        this.nomeMateria = nomeMateria;
        this.etapaList = etapaList;
        this.anim = false;
        this.isExpanded = false;
    }

    public String getNomeMateria() {
        return nomeMateria;
    }

    public List<Etapa> getEtapaList() {
        return etapaList;
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
