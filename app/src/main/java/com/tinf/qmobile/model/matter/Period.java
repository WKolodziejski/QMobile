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
    private float gradeRP_ = -1;
    private float gradeFinal_ = -1;
    private int absences_ = -1;
    public ToOne<Matter> matter;
    public ToMany<Journal> journals;
    public ToMany<Aula> aulas;
    
    public Period(String title){
        this.title_ = title;
    }

    public String getGrade() {
        return grade_ == -1 ? "" : String.valueOf(grade_);
    }

    public String getGradeRP() {
        return gradeRP_ == -1 ? "" : String.valueOf(gradeRP_);
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

    public void setGradeRP(float gradeRP) {
        this.gradeRP_ = gradeRP;
    }

    public void setGradeFinal(float gradeFinal) {
        this.gradeFinal_ = gradeFinal;
    }

    public void setAbsences(int absences) {
        this.absences_ = absences;
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

    public float getGradeRP_() {
        return gradeRP_;
    }

    public float getGradeFinal_() {
        return gradeFinal_;
    }

    public int getAbsences_() {
        return absences_;
    }

}
