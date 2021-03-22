package com.tinf.qmobile.model.matter;

import androidx.annotation.ColorInt;

import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.Journal;
import com.tinf.qmobile.model.material.Material;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

import static com.tinf.qmobile.model.ViewType.HEADER;

@Entity
public class Matter implements Queryable {
    @Transient public boolean isExpanded;
    @Transient public boolean shouldAnimate;
    @Id public long id;
    @ColorInt private int color_;
    private String title_;
    private String description_;
    private String situation_;
    private int absences_ = -1;
    private float mean_ = -1;
    private int year_;
    private int period_;
    public ToMany<Period> periods;
    public ToMany<Schedule> schedules;
    public ToMany<Material> materials;
    public ToOne<Infos> infos;
    //public ToOne<Clazz> clazz;

    public Matter(String description, int color, int year, int period) {
        this.description_ = description;
        this.color_ = color;
        this.year_ = year;
        this.period_ = period;
    }

    public String getAbsences() {
        return absences_ == -1 ? "-" : String.valueOf(absences_);
    }

    public String getMean() {
        return mean_ == -1 ? "-" : String.valueOf(mean_);
    }

    public void setAbsences(int absences) {
        this.absences_ = absences;
    }

    public void setMean(float mean) {
        this.mean_ = mean;
    }

    @ColorInt
    public int getColor() {
        return color_;
    }

    public void setColor(int color) {
        this.color_ = color;
    }

    public String getTitle() {
        return title_ == null ? description_ : title_;
    }

    public String getSituation() {
        return situation_ == null ? "" : situation_;
    }

    public void setTitle(String title) {
        this.title_ = title;
    }

    public void setSituation(String situation) {
        this.situation_ = situation;
    }

    public Period getLastPeriod() {
        int k = 0;

        for (int j = 0; j < periods.size(); j++) {
            if (!periods.get(j).journals.isEmpty() && !periods.get(j).isSub_()) {
                k = j;
            }
        }

        return periods.get(k);
    }

    public int getJournalNotSeenCount() {
        Period period = getLastPeriod();

        int sum = 0;

        for (Journal j : period.journals)
            if (!j.isSeen_())
                sum++;

        return sum;
    }

    public int getMaterialNotSeenCount() {
        int sum = 0;

        for (Material m : materials)
            if (!m.isSeen_())
                sum++;

        return sum;
    }

    public int getQID() {
        return Integer.parseInt(description_.substring(0, description_.indexOf('-') - 1));
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

    public float getMean_() {
        return mean_;
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

    public String getSituation_() {
        return situation_;
    }

    @Override
    public int getItemType() {
        return HEADER;
    }

}
