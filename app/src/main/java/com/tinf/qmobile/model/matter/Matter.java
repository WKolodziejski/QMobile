package com.tinf.qmobile.model.matter;

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
    private int absences_ = -1;
    private int year_;
    private int period_;
    public ToMany<Period> periods;
    public ToMany<Schedule> schedules;
    public ToMany<Material> materials;
    public ToOne<Infos> infos;
    public ToOne<Clazz> clazz;

    public Matter(String description, int color, int year, int period) {
        this.description_ = description;
        this.color_ = color;
        this.year_ = year;
        this.period_ = period;
    }

    public String getAbsences() {
        return absences_ == -1 ? "" : String.valueOf(absences_);
    }

    public void setAbsences(int absences) {
        this.absences_ = absences;
    }

    @ColorInt
    public int getColor() {
        return color_;
    }

    public void setColor(int color) {
        this.color_ = color;
    }

    public String getTitle() {
        return title_ == null ? "" : title_;
    }

    public void setTitle(String title) {
        this.title_ = title;
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

    public String getDescription_() {
        return description_;
    }

}
