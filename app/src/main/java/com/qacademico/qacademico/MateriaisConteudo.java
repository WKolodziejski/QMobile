package com.qacademico.qacademico;

/**
 * Created by User on 15/03/2018.
 */

public class MateriaisConteudo {
    private String data;
    private String nomeConteudo;
    private String link;
    private String descricao; // pode ou não ter descrição

    public MateriaisConteudo(String data, String nomeConteudo, String link, String descricao) {
        this.data = data;
        this.nomeConteudo = nomeConteudo;
        this.link = link;
        this.descricao = descricao;
    }

    public String getNomeConteudo() {
        return nomeConteudo;
    }

    public String getLink() {
        return link;
    }

    public String getData() {
        return data;
    }

    public String getDescricao() {
        return descricao;
    }

}
