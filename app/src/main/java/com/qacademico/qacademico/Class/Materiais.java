package com.qacademico.qacademico.Class;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.List;

public class Materiais implements Serializable {
    private final String data;
    private final String nomeConteudo;
    private final String link;
    private final String descricao;
    private final int tint;
    private final Drawable icon;
    private boolean isExpanded;
    private boolean anim;

    public Materiais(String data, String nomeConteudo, String link, String descricao, int tint, Drawable icon) {
        this.data = data;
        this.nomeConteudo = nomeConteudo;
        this.link = link;
        this.descricao = descricao;
        this.anim = false;
        this.isExpanded = false;
        this.tint = tint;
        this.icon = icon;
    }

    public String getData() {
        return data;
    }

    public String getNomeConteudo() {
        return nomeConteudo;
    }

    public String getLink() {
        return link;
    }

    public String getDescricao() {
        return descricao;
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

    public int getTint() {
        return tint;
    }

    public Drawable getIcon() {
        return icon;
    }
}
