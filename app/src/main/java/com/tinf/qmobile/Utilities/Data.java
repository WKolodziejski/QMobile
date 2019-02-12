package com.tinf.qmobile.Utilities;

import android.app.Application;
import com.tinf.qmobile.App;
import com.tinf.qmobile.Class.Materias.Materia;
import com.tinf.qmobile.Class.Materias.Materia_;
import com.tinf.qmobile.Network.Client;

import io.objectbox.BoxStore;
import io.objectbox.query.Query;

public class Data {

    public static Query<Materia> getMaterias() {
        return App.getBox().boxFor(Materia.class).query().order(Materia_.name)
                .equal(Materia_.year, Client.getYear()).build();
    }

}
