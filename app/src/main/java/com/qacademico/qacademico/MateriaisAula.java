package com.qacademico.qacademico;

import java.util.ArrayList;

public class MateriaisAula {
    private String ano;
    private ArrayList<MateriaisMateria> materias;

    public ArrayList<MateriaisMateria> getMateria() {
        return materias;
    }

    public void clearMaterias(){
        materias.clear();
    }
    public void addMateria(MateriaisMateria materia){
        materias.add(materia);
    }
}
