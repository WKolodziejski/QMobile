package com.qacademico.qacademico.Class;

/*import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;*/
import java.io.Serializable;
import java.util.List;

public class Etapa implements Serializable {
    private final String aux;
    private final List<Diarios> diariosList;

    public Etapa(String aux, List<Diarios> diariosList){
        this.aux = aux;
        this.diariosList = diariosList;
    }

    public List<Diarios> getDiariosList() {
        return diariosList;
    }

    public String getAux() {
        return aux;
    }

}
