package com.tinf.qmobile.model.matter;

import static com.tinf.qmobile.model.ViewType.PERIOD;

import com.tinf.qmobile.model.Queryable;
import com.tinf.qmobile.model.journal.Journal;

import java.util.Locale;
import java.util.Objects;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Period implements Queryable {
    @Id public long id;
    private String title_;
    private float grade_ = -1;
    private float gradeFinal_ = -1;
    private int absences_ = -1;
    private boolean isSub_;
    public ToOne<Matter> matter;
    public ToMany<Journal> journals;
    public ToMany<Clazz> classes;

    public Period(String title){
        this.title_ = title;
    }

    public String getGrade() {
        return grade_ == -1 ? "-" : String.format(Locale.getDefault(), "%.1f", grade_);
    }

    public String getGradeFinal() {
        return gradeFinal_ == -1 ? "-" : String.format(Locale.getDefault(), "%.1f", gradeFinal_);
    }

    public String getAbsences() {
        return absences_ == -1 ? "-" : String.valueOf(absences_);
    }

    public String getTitle() {
        return title_ == null ? "-" : title_;
    }

    public void setTitle(String title) {
        this.title_ = title;
    }

    public void setGrade(float grade) {
        this.grade_ = grade;
    }

    public void setGradeFinal(float gradeFinal) {
        this.gradeFinal_ = gradeFinal;
    }

    public void setAbsences(int absences) {
        this.absences_ = absences;
    }

    public float getGradeSum() {
        if (journals.isEmpty())
            return -1;

        float sum = 0;

        for (Journal j : journals)
            if (j.getGrade_() != -1)
                sum += j.getGrade_();

        return sum;
    }

    public String getGradeSumString() {
        float p = getGradeSum();

        return p == -1 ? "-" : String.format(Locale.getDefault(), "%.1f", p);
    }

    public float getPlotGrade() {
        if (journals.isEmpty())
            return 0;

        float sum = getGradeSum();

        float weight = 0;

        for (Journal j : journals)
            if (j.getWeight_() != -1)
                weight += j.getMax_();

        return (sum / weight) * 10;
    }

    public String getLabel() {
        return String.format(Locale.getDefault(), "%.1f", getPlotGrade());
    }

    public void setSub() {
        isSub_ = true;
    }

    @Override
    public int getItemType() {
        return PERIOD;
    }

    /*
     * Required methods
     */

    public Period() {}

    public String getTitle_() {
        return title_;
    }

    public float getGrade_() {
        return grade_;
    }

    public float getGradeFinal_() {
        return gradeFinal_;
    }

    public int getAbsences_() {
        return absences_;
    }

    public boolean isSub_() {
        return isSub_;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean isSame(Queryable queryable) {
        return queryable.equals(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Period)) return false;
        Period period = (Period) o;
        return  id == period.id &&
                Float.compare(period.getGrade_(), getGrade_()) == 0 &&
                Float.compare(period.getGradeFinal_(), getGradeFinal_()) == 0 &&
                getAbsences_() == period.getAbsences_() &&
                isSub_() == period.isSub_() &&
                matter.getTarget().equals(period.matter.getTarget()) &&
                Objects.equals(getTitle_(), period.getTitle_());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title_, grade_, gradeFinal_, absences_, isSub_);
    }

}
