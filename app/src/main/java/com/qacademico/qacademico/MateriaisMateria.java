package com.qacademico.qacademico;

import java.util.ArrayList;

/**
 * Created by User on 15/03/2018.
 */

public class MateriaisMateria {
    private String nomeMateria;
    private ArrayList<MateriaisConteudo> listaMateriais;

    public MateriaisMateria(String nomeMateria) {
        this.nomeMateria = nomeMateria;
    }

    public String getNomeMateria() {
        return nomeMateria;
    }

    public void clearMateriais(){
        listaMateriais.clear();
    }

    public void addMateriais(MateriaisConteudo material){
        listaMateriais.add(material);
    }


}
