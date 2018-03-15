package com.qacademico.qacademico;

import java.util.List;

public class Material {
    private final String data;
    private final String nomeConteudo;
    private final String link;
    private final String descricao;

    public Material(String data, String nomeConteudo, String link, String descricao) {
        this.data = data;
        this.nomeConteudo = nomeConteudo;
        this.link = link;
        this.descricao = descricao;
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
}
