package com.qacademico.qacademico;

/*import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;*/
import java.util.List;

public class Etapa /*implements Parcelable*/{
    private final String aux;
    private final List<Trabalho> trabalhoList;

    public Etapa(String aux, List<Trabalho> trabalhoList){
        this.aux = aux;
        this.trabalhoList = trabalhoList;
    }

    public List<Trabalho> getTrabalhoList() {
        return trabalhoList;
    }

    public String getAux() {
        return aux;
    }

}
