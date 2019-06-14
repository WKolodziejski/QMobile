package com.tinf.qmobile.model.matter;

import com.tinf.qmobile.model.calendario.EventJournal;
import com.tinf.qmobile.model.materiais.Material;
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
    @ColorInt private int color_;
    private String title_;
    private String description_;
    private String clazz_;
    private int absences_ = -1;
    private int year_;
    private int period_;
    private int qid_;
    public ToMany<Period> periods;
    public ToMany<Schedule> schedules;
    public ToMany<EventJournal> events;
    public ToMany<Material> materials;
    public ToOne<Infos> infos;
    public ToOne<Clazz> clazz;

    public Matter(String title, int color, int year, int period, int qid) {
        this.title_ = title;
        this.color_ = color;
        this.year_ = year;
        this.period_ = period;
        this.qid_ = qid;
    }

    public String getAbsences() {
        return absences_ == -1 ? "" : String.valueOf(absences_);
    }

    public void setAbsences(int absences) {
        this.absences_ = absences;
    }

    public void setDescription(String description) {
        this.description_ = description;
    }

    public void setClazz(Clazz clazz) {
        this.clazz.setTarget(clazz);
        this.clazz_ = clazz.getTitle_();
    }

    @ColorInt
    public int getColor() {
        return color_;
    }

    public String getTitle() {
        return title_ == null ? "" : title_;
    }

    /*
     * Auto-generated methods
     */

    public Matter() {}

    public int getColor_() {
        return color_;
    }

    public String getTitle_() {
        return title_;
    }

    public int getAbsences_() {
        return absences_;
    }

    public int getYear_() {
        return year_;
    }

    public int getPeriod_() {
        return period_;
    }

    public int getQid_() {
        return qid_;
    }

    public String getDescription_() {
        return description_;
    }

    public String getClazz_() {
        return clazz_;
    }

}
