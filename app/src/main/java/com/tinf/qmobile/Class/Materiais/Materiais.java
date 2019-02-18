package com.tinf.qmobile.Class.Materiais;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Materiais implements Serializable {
    private final String data;
    private final String nomeConteudo;
    private final String link;
    private final String descricao;
    private final String extension;
    private final int icon;
    private boolean isOffline;

    public Materiais(String data, String nomeConteudo, String link, String descricao, String extension, int icon) {
        this.data = data;
        this.nomeConteudo = nomeConteudo;
        this.link = link;
        this.descricao = descricao;
        this.extension = extension;
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

    public int getIcon() {
        return icon;
    }

    public String getExtension() {
        return extension;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean isoffline) {
        this.isOffline = isoffline;
    }
}
