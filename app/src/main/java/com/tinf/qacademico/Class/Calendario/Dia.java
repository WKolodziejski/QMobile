package com.tinf.qacademico.Class.Calendario;


import java.io.Serializable;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Dia implements Serializable {
    @Id public long id;
    @Backlink(to = "day") public ToMany<Evento> eventos;
    private int day;
    public ToOne<Mes> mes;

    public Dia(){};

    public Dia(int day) {
        this.day = day;
    }

    public int getDay() {
        return day;
    }
}
