package com.tinf.qmobile.Class.Materias;

import com.tinf.qmobile.Class.Calendario.EventJournal;
import com.tinf.qmobile.Class.Materiais.Material;
import androidx.annotation.ColorInt;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Matter {
    @Transient public boolean isExpanded;
    @Transient public boolean shouldAnimate;
    @Id public long id;
    @ColorInt private int color;
    private String title;
    private int absences = -1;
    private int year;
    private int period;
    public ToMany<Period> periods;
    public ToMany<Schedule> schedules;
    public ToMany<EventJournal> events;
    public ToMany<Material> materials;
    public ToOne<Infos> infos;

    public Matter(String title, int color, int year, int period) {
        this.title = title;
        this.color = color;
        this.year = year;
        this.period = period;
    }

    public String getAbsencesString() {
        return absences == -1 ? "" : String.valueOf(absences);
    }

    /*
     * Auto-generated methods
     */

    public Matter() {}

    public int getYear() {
        return year;
    }

    public int getColor() {
        return color;
    }

    public String getTitle() {
        return title;
    }

    public int getAbsences() {
        return absences;
    }

    public void setAbsences(int absences) {
        this.absences = absences;
    }

    public int getPeriod() {
        return period;
    }

}
