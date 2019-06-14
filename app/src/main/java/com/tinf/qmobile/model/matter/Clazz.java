package com.tinf.qmobile.model.matter;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Clazz {
    @Id public long id;
    private String title_;
    public ToMany<Matter> matters;

    public Clazz(String title) {
        this.title_ = title;
    }

    /*
     * Auto-generated methods
     */

    public Clazz() {}

    public String getTitle_() {
        return title_;
    }

}
