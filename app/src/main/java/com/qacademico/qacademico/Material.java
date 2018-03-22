package com.qacademico.qacademico;

import java.io.Serializable;
import java.util.List;

public class Material implements Serializable {
    private final String data;
    private final String nomeConteudo;
    private final String link;
    private final String descricao;
    private boolean isExpanded;
    private boolean anim;

    public Material(String data, String nomeConteudo, String link, String descricao) {
        this.data = data;
        this.nomeConteudo = nomeConteudo;
        this.link = link;
        this.descricao = descricao;
        this.anim = false;
        this.isExpanded = false;
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

    public String getExtension(){
        return getLink().substring(getLink().indexOf("."));
    }
}
