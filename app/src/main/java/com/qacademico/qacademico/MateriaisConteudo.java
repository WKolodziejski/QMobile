package com.qacademico.qacademico;

/**
 * Created by User on 15/03/2018.
 */

public class MateriaisConteudo {
    private String data;
    private String nomeConteudo;
    private String link;
    private String descricao; // pode ou não ter descrição
    public boolean temDesc;

    public MateriaisConteudo(String data, String nomeConteudo, String link) {
        this.data = data;
        this.nomeConteudo = nomeConteudo;
        this.link = link;
        this.descricao = null;
        this.temDesc = false;
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

    public void setDescricao(String descricao) {
        this.descricao = descricao;
        temDesc = true;
    }
}
