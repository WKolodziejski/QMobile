package com.qacademico.qacademico;


import java.util.List;

public class Materiais {
    private final String nomeMateria;
    private final List<Material> materialList;

    public Materiais(String nomeMateria, List<Material> materialList) {
        this.nomeMateria = nomeMateria;
        this.materialList = materialList;
    }

    public String getNomeMateria() {
        return nomeMateria;
    }

    public List<Material> getMaterialList() {
        return materialList;
    }
}
