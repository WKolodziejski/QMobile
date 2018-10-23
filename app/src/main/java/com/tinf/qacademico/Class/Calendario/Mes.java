package com.tinf.qacademico.Class.Calendario;


import java.io.Serializable;
import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

@Entity
public class Mes implements Serializable {
    @Id public long id;
    @Backlink(to = "mes") public ToMany<Dia> days;
    private int month;
    private int year;

    public Mes(){};

    public Mes(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
