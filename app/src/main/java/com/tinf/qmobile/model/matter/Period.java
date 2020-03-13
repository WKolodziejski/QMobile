package com.tinf.qmobile.model.matter;

import com.tinf.qmobile.model.journal.Journal;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Period {
    @Id public long id;
    private String title_;
    private float grade_ = -1;
    private float gradeFinal_ = -1;
    private int absences_ = -1;
    private boolean isSub_;
    public ToOne<Matter> matter;
    public ToMany<Journal> journals;
    public ToMany<Aula> aulas;

    public Period(String title){
        this.title_ = title;
    }

    public String getGrade() {
        return grade_ == -1 ? "" : String.valueOf(grade_);
    }

    public String getGradeFinal() {
        return gradeFinal_ == -1 ? "" : String.valueOf(gradeFinal_);
    }

    public String getAbsences() {
        return absences_ == -1 ? "" : String.valueOf(absences_);
    }

    public String getTitle() {
        return title_ == null ? "" : title_;
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

    public float getPartialGrade() {
        if (journals.isEmpty())
            return -1;

        float sum = getGradeSum();

        float weight = 0;

        for (Journal j : journals)
            if (j.getWeight_() != -1)
                weight = +j.getWeight_();

        return sum / weight;
    }

    public void setSub() {
        isSub_ = true;
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

}
